package gov.nysenate.openleg.spotchecks.daybreak;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportService;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakBill;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakDao;
import gov.nysenate.openleg.spotchecks.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType.REFERENCE_DATA_MISSING;
import static java.util.stream.Collectors.toSet;

/**
 * SpotCheckReportService implementation that utilizes the DaybreakCheckService to generate
 * and save reports for bill data.
 */
@Service("daybreakReport")
public class DaybreakReportService implements SpotCheckReportService<BaseBillId> {
    private static final Logger logger = LoggerFactory.getLogger(DaybreakReportService.class);
    private final DaybreakCheckService daybreakCheckService;
    private final DaybreakDao daybreakDao;
    private final BillDataService billDataService;

    @Autowired
    public DaybreakReportService(DaybreakCheckService daybreakCheckService, DaybreakDao daybreakDao, BillDataService billDataService) {
        this.daybreakCheckService = daybreakCheckService;
        this.daybreakDao = daybreakDao;
        this.billDataService = billDataService;
    }

    /** --- Implemented Methods --- */

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_DAYBREAK;
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        // Create a new report instance
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        // Fetch the daybreak bills that are within the given date range
        logger.info("Fetching daybreak bills...");
        Range<LocalDate> dateRange = Range.closed(start.toLocalDate(), end.toLocalDate());
        List<DaybreakBill> daybreakBills = daybreakDao.getCurrentDaybreakBills(dateRange);
        if (daybreakBills.isEmpty()) {
            throw new ReferenceDataNotFoundEx("The collection of daybreak bills within the given date range is empty.");
        }
        // All daybreak bills should have the same reference date.
        SpotCheckReferenceId refId = daybreakBills.get(0).getReferenceId();

        // The report date/time should be truncated to the second to make it easier to query
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK,
                refId.getRefActiveDateTime(),
                LocalDateTime.now()));

        logger.info("Using Daybreak {} to generate report", refId);
        // Create a set of the base bill ids from the daybreak bills
        Set<BaseBillId> daybreakBillIds = daybreakBills.stream()
            .map(DaybreakBill::getBaseBillId).collect(Collectors.toSet());

        // And a set of all of our bill ids (excluding resolutions) present in the backing store
        Set<BaseBillId> openlegBillIds =
            billDataService.getBillIds(SessionYear.current(), LimitOffset.ALL).stream()
                .filter(id -> !id.getBillType().isResolution())
                .collect(toSet());

        // Check for differences between the set of daybreak and openleg base bill ids.
        Sets.symmetricDifference(daybreakBillIds, openlegBillIds)
                .forEach(id -> {
                    // id exists in daybreak or openleg sets, never both.
                    SpotCheckObservation<BaseBillId> sourceMissingObs = new SpotCheckObservation<>(refId, id);
                    SpotCheckMismatch mismatch;
                    if (openlegBillIds.contains(id)) {
                        // openleg has the bill but daybreak does not, add reference missing mismatch if bill is published.
                        BillInfo bill = billDataService.getBillInfo(id);
                        if (billIsPublished(bill)) {
                            logger.info("Missing Daybreak bill {}", id);
                            mismatch = new SpotCheckMismatch(REFERENCE_DATA_MISSING, id, "");
                            recordMismatch(report, sourceMissingObs, mismatch);
                        }
                    }
                    else {
                        // daybreak has the bill but openleg does not, add observe missing mismatch.
                        logger.info("Missing OpenLeg bill {}", id);
                        mismatch = new SpotCheckMismatch(OBSERVE_DATA_MISSING, "", "");
                        recordMismatch(report, sourceMissingObs, mismatch);
                    }
                });

        // Perform actual spot checks for the bills common to both sets
        daybreakBills.stream()
            .filter(daybreakBill -> openlegBillIds.contains(daybreakBill.getBaseBillId()))
            .forEach(daybreakBill -> {
                Bill bill = billDataService.getBill(daybreakBill.getBaseBillId());
                report.addObservation(daybreakCheckService.check(bill, daybreakBill));
            });
        // Set the report as being checked
        daybreakDao.updateDaybreakReportSetChecked(report.getReferenceDateTime().toLocalDate(), true);
        // Done with this report!
        return report;
    }

    /** --- Internal Methods --- */

    private boolean billIsPublished(BillInfo billInfo) {
        Version activeVersion = billInfo.getActiveVersion();
        PublishStatus pubStatus = billInfo.getAmendPublishStatusMap().get(activeVersion);
        return pubStatus != null && pubStatus.isPublished();
    }

    private void recordMismatch(SpotCheckReport<BaseBillId> report, SpotCheckObservation<BaseBillId> sourceMissingObs, SpotCheckMismatch mismatch) {
        sourceMissingObs.addMismatch(mismatch);
        report.addObservation(sourceMissingObs);
    }
}