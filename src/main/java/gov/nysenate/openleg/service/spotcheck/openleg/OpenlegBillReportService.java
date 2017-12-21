package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.reference.openleg.OpenlegBillDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

import java.util.List;
import java.util.Set;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.OBSERVE_DATA_MISSING;
import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.REFERENCE_DATA_MISSING;

/**
 * Created by Chenguang He on 2017/3/20.
 * This service is used to report the difference of two openleg branches.
 */
@Service("openlegBillReport")

public class OpenlegBillReportService extends BaseSpotCheckReportService<BaseBillId> {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegBillReportService.class);

    @Autowired
    private SpotCheckReportDao<BaseBillId> reportDao;

    @Autowired
    private OpenlegBillDao openlegBillDao;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegBillCheckService checkService;

    @Autowired
    Environment env;

    @Override
    protected SpotCheckReportDao<BaseBillId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_BILL;
    }

    /**
     * generate report of difference between xml-processing bill and sobi-processing bill.
     *
     * @param start LocalDateTime - The session year
     * @param end   LocalDateTime -  Not in use
     * @return the mismatch
     * @throws Exception exception
     */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        // Create a new report instance
        logger.info("Start generating new report of difference between Openleg Ref and XML branch ");
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_BILL,
                LocalDateTime.now(),
                LocalDateTime.now());
        report.setReportId(reportId);
        logger.info("Loading BillView from Openleg reference");
        logger.info("The current session year is " + SessionYear.of( start.getYear() ) );
        int totalRefBills = openlegBillDao.getTotalRefBillsForSessionYear(start.getYear(), env.getOpenlegRefApiKey());
        int offsetRefBills = 0;

        //Fetching Bill from Openleg Source branch
        Set<BaseBillId> localBill = new HashSet<>();
        localBill.addAll( billDataService.getBillIds(SessionYear.of(start.getYear()), LimitOffset.ALL));


        while (offsetRefBills < totalRefBills) {

            List<BillView> referenceBillViews = openlegBillDao.getOpenlegBillView(String.valueOf(start.getYear()), env.getOpenlegRefApiKey(), offsetRefBills);
            if (referenceBillViews.isEmpty()) {
                throw new ReferenceDataNotFoundEx("The collection of sobi bills with the given session year " + SessionYear.of( start.getYear() )  + " is empty.");
            }

            //Get BaseBillId of BillView from Openleg reference by iterating through them
            logger.info("Check the symmetric diff...");
            Set<BaseBillId> refBill = new HashSet<>();

            for (BillView sobiBill : referenceBillViews) {
                refBill.add(sobiBill.toBaseBillId());
            }
            logger.info("Retrieved " + refBill.size()+" bills in Openleg-Ref");


            Set<BaseBillId> diffBill = new HashSet<>(); // the collection of bills which only appears in both ref or source

            for (BaseBillId refBillId:refBill) {
                SpotCheckObservation<BaseBillId> sourceMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), refBillId);
                if (!localBill.contains(refBillId)) {
                    localBill.remove(refBillId);
                    sourceMissingObs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, refBillId, "Missing Data from Openleg XML, ID:" + refBillId.getBasePrintNo()));
                    report.addObservation(sourceMissingObs);
                }
                else {
                    localBill.remove(refBillId);
                    diffBill.add(refBillId);
                }
            }


            logger.info("Comparing Bill from Openleg Source branch by iterating BaseBillId of BillView from Openleg Ref");
            for (BillView sobiBill : referenceBillViews) {
                if (!diffBill.contains(sobiBill.toBaseBillId())) // if current bill appears in both dev and xml.
                    continue;
                SpotCheckObservation<BaseBillId> observation = checkService.check(new BillView(billDataService.getBill(sobiBill.toBaseBillId())),sobiBill);

                report.addObservation(observation);
            }

            offsetRefBills = offsetRefBills + 1000;
        }

        for (BaseBillId refMissingId: localBill) {
            SpotCheckObservation<BaseBillId> refMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), refMissingId);
            refMissingObs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, refMissingId, "Missing Data from Openleg Ref, ID:" + refMissingId.getBasePrintNo()));
        }
        logger.info("Found total number of " + report.getOpenMismatchCount(false) + " mismatches");
        return report;
    }

}
