package gov.nysenate.openleg.service.spotcheck.daybreak;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.REFERENCE_DATA_MISSING;
import static java.util.stream.Collectors.toSet;

/**
 * SpotCheckReportService implementation that utilizes the DaybreakCheckService to generate
 * and save reports for bill data.
 */
@Service("daybreakReport")
public class DaybreakReportService implements SpotCheckReportService<BaseBillId>
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakReportService.class);

    @Autowired
    private DaybreakCheckService daybreakCheckService;

    @Autowired
    private DaybreakDao daybreakDao;

    @Autowired
    private SpotCheckReportDao<BaseBillId> reportDao;

    @Autowired
    private BillDataService billDataService;

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
        List<DaybreakBill> daybreakBills;
        try {
            daybreakBills = daybreakDao.getCurrentDaybreakBills(dateRange);
        }
        catch (DataAccessException ex) {
            throw new ReferenceDataNotFoundEx("Failed to retrieve daybreak bills within the given date range.");
        }
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
        Sets.symmetricDifference(daybreakBillIds, openlegBillIds).stream()
                .forEach(id -> {
                    // id exists in daybreak or openleg sets, never both.
                    SpotCheckObservation<BaseBillId> sourceMissingObs = new SpotCheckObservation<>(refId, id);
                    SpotCheckMismatch mismatch = null;
                    if (openlegBillIds.contains(id)) {
                        // openleg has the bill but daybreak does not, add reference missing mismatch if bill is published.
                        Bill bill = billDataService.getBill(id);
                        if (billIsPublished(bill)) {
                            logger.info("Missing Daybreak bill {}", id);
                            mismatch = new SpotCheckMismatch(REFERENCE_DATA_MISSING, "", id.toString());
                            recordMismatch(report, sourceMissingObs, mismatch);
                        }
                    }
                    else {
                        // daybreak has the bill but openleg does not, add observe missing mismatch.
                        logger.info("Missing OpenLeg bill {}", id);
                        mismatch = new SpotCheckMismatch(OBSERVE_DATA_MISSING, id.toString(), "");
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
        // Done with this report!
        return report;
    }

    private boolean billIsPublished(Bill bill) {
        Version activeVersion = bill.getActiveVersion();
        Optional<PublishStatus> pubStatus = bill.getPublishStatus(activeVersion);
        return pubStatus.isPresent() && pubStatus.get().isPublished();
    }

    private void recordMismatch(SpotCheckReport<BaseBillId> report, SpotCheckObservation<BaseBillId> sourceMissingObs, SpotCheckMismatch mismatch) {
        sourceMissingObs.addMismatch(mismatch);
        report.addObservation(sourceMissingObs);
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<BaseBillId> report) {
        if (report == null) {
            throw new IllegalArgumentException("Supplied report cannot be null.");
        }
        reportDao.saveReport(report);
        daybreakDao.updateDaybreakReportSetChecked(report.getReferenceDateTime().toLocalDate(), true);
    }

    /** {@inheritDoc}
     * @param reportId*/
    @Override
    public SpotCheckReport<BaseBillId> getReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null!");
        }
        try {
            return reportDao.getReport(reportId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new SpotCheckReportNotFoundEx(reportId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType reportType, LocalDateTime start, LocalDateTime end,
                                                           SortOrder dateOrder) {
        if (dateOrder == null) { dateOrder = SortOrder.ASC; }
        return reportDao.getReportSummaries(reportType, start, end, dateOrder);
    }

    @Override
    public SpotCheckOpenMismatches<BaseBillId> getOpenObservations(OpenMismatchQuery query) {
        return reportDao.getOpenObservations(query);
    }

    /** {@inheritDoc}
     * @param reportId*/
    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null");
        }
        reportDao.deleteReport(reportId);
    }
}