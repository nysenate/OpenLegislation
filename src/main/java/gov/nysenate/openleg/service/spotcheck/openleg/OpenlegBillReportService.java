package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.bill.reference.openleg.OpenlegBillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chenguang He on 2017/3/20.
 * This service is used to report the difference of two openleg branches.
 */
@Service("openlegBillReport")
public class OpenlegBillReportService implements SpotCheckReportService<BaseBillId> {

    private static final Logger logger = LoggerFactory.getLogger(OpenlegBillReportService.class);

    /** Throttles the number of bills retrieved at once from the ref. api to reduce memory footprint. */
    private static final int billRetrievalLimit = 100;

    private final OpenlegBillDao openlegBillDao;
    private final BillDataService billDataService;
    private final OpenlegBillCheckService checkService;
    private final Environment env;

    @Autowired
    public OpenlegBillReportService(OpenlegBillDao openlegBillDao,
                                    BillDataService billDataService,
                                    OpenlegBillCheckService checkService,
                                    Environment env) {
        this.openlegBillDao = openlegBillDao;
        this.billDataService = billDataService;
        this.checkService = checkService;
        this.env = env;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_BILL;
    }

    /**
     * Generate report checking against bills from another openleg instance.
     *
     * @param start LocalDateTime - The session year
     * @param end   LocalDateTime -  Not in use
     * @return {@link SpotCheckReport<BaseBillId>}
     */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) {
        // Create a new report instance
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_BILL,
                LocalDateTime.now(),
                LocalDateTime.now());
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>(reportId);

        SessionYear sessionYear = SessionYear.of(start);

        logger.info("Running Bill Spotcheck against {} for {} session...", env.getOpenlegRefUrl(), sessionYear);

        // Get a set of all local bill ids for the session for tracking ref. missing mismatches.
        Set<BaseBillId> localBillIds = new HashSet<>(billDataService.getBillIds(sessionYear, LimitOffset.ALL));

        // Initialize to 1 but set to the real value once a response has been read.
        int totalRefBills = 1;

        // Go through all bills of the session in paginated increments.
        for (LimitOffset limoff = new LimitOffset(billRetrievalLimit);
             limoff.getOffsetStart() <= totalRefBills;
             limoff = limoff.next()) {

            // Get bills from ref. API
            PaginatedList<BillView> paginatedBillViews = openlegBillDao.getBillViews(sessionYear, limoff);
            // Set the total based on the response.
            totalRefBills = paginatedBillViews.getTotal();

            logger.info("Checking bills {} - {} of {}",
                    limoff.getOffsetStart(), limoff.getOffsetEnd(), totalRefBills);

            // Check each bill in the result.
            for (BillView refBill : paginatedBillViews.getResults()) {
                BaseBillId baseBillId = refBill.toBaseBillId();
                try {
                    if (!localBillIds.contains(baseBillId)) {
                        throw new BillNotFoundEx(baseBillId);
                    }
                    BillView localBill = new BillView(billDataService.getBill(baseBillId), Sets.newHashSet(BillTextFormat.PLAIN));
                    SpotCheckObservation<BaseBillId> obs = checkService.check(localBill, refBill);
                    report.addObservation(obs);
                    // Remove this bill from localBillIds to indicate it was present in ref. bills.
                    localBillIds.remove(baseBillId);
                } catch (BillNotFoundEx ex) {
                    // Add data missing mismatch if the bill was not found locally.
                    report.addObservedDataMissingObs(baseBillId);
                }
            }
        }

        // Set any remaining unchecked local bill ids as ref. missing mismatches
        for (BaseBillId id : localBillIds) {
            if (report.getObservationMap().containsKey(id)) {
                throw new IllegalStateException(id + " is supposedly not checked, but an observation for it exists");
            }
            BillInfo billInfo = billDataService.getBillInfo(id);
            if (billInfo.isBaseVersionPublished()) {
                report.addRefMissingObs(id);
            } else {
                report.addEmptyObservation(id);
            }
        }

        return report;
    }

}
