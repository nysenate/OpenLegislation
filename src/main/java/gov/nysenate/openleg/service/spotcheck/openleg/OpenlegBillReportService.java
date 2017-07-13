package gov.nysenate.openleg.service.spotcheck.openleg;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.reference.openlegdev.OpenlegDevDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${openleg.apiKey}")
    private  String apiKey;

    @Autowired
    private SpotCheckReportDao<BaseBillId> reportDao;

    @Autowired
    private OpenlegDevDao openlegDevDao;

    @Autowired
    private BillDataService billDataService;

    @Autowired
    OpenlegBillCheckService checkService;

    @Override
    protected SpotCheckReportDao<BaseBillId> getReportDao() {
        return reportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.OPENLEG_DEV;
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
        logger.info("Start generating new report of difference between Openleg Dev and XML branch ");
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.OPENLEG_DEV,
                LocalDateTime.now(),
                LocalDateTime.now());
        report.setReportId(reportId);
        logger.info("Loading BillView from Openleg Dev ");
        List<BillView> referenceBillViews = openlegDevDao.getOpenlegBillView(String.valueOf(start.getYear()), apiKey);
        if (referenceBillViews.isEmpty())
            throw new ReferenceDataNotFoundEx("The collection of sobi bills with the given session year is empty.");
        //Fetching Bill from Openleg xml-data-processing branch by iterating BaseBillId of BillView from Openleg Dev
        logger.info("Check the sym diff...");
        Set<BaseBillId> refBill = new HashSet<>();
        Set<BaseBillId> localBill = new HashSet<>();
        for (BillView sobiBill : referenceBillViews) {
            refBill.add(sobiBill.toBaseBillId());
        }
        //get all
        for (BaseBillId baseBillId : billDataService.getBillIds(SessionYear.of(start.getYear()), LimitOffset.ALL)) {
            localBill.add(baseBillId);
        }
        Set<BaseBillId> diffBill = new HashSet<>(); // the collection of bills which only appears in either dev or xml
        // Check for differences between the set of daybreak and openleg base bill ids.
        logger.info("Fetching Missing Bill");
        final long[] numOfMismatches = {0};
        System.out.println("Found " + refBill.size()+" bills in Openleg-dev(SOBI) and " + localBill.size()+" bills in local (XML)" );
        Sets.symmetricDifference(refBill, localBill).stream()
                .forEach(id -> {
                    SpotCheckObservation<BaseBillId> sourceMissingObs = new SpotCheckObservation<>(reportId.getReferenceId(), id);
                    if (localBill.contains(id)) {
                        sourceMissingObs.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING, id, "Missing Data from Openleg Dev, ID:" + id.getBasePrintNo()));

                    } else {
                        sourceMissingObs.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, id, "Missing Data from Openleg XML, ID:" + id.getBasePrintNo()));
                    }
                    diffBill.add(id);
                    sourceMissingObs.setReferenceId(reportId.getReferenceId());
                    sourceMissingObs.setObservedDateTime(LocalDateTime.now());
                    numOfMismatches[0] += sourceMissingObs.getMismatches().size();
                    report.addObservation(sourceMissingObs);
                });
        logger.info("Found " + numOfMismatches[0] +" missing bills mismatches");
        numOfMismatches[0] = 0; //reset to 0
        logger.info("Fetching Bill from Openleg xml-data-processing branch by iterating BaseBillId of BillView from Openleg Dev");
        for (BillView sobiBill : referenceBillViews) {
            if (diffBill.contains(sobiBill.toBaseBillId())) // if current bill appears in both dev and xml.
                continue;
            SpotCheckObservation<BaseBillId> observation = checkService.check(sobiBill, new BillView(billDataService.getBill(sobiBill.toBaseBillId())));
            // one observation consists of multiple mismatches
            SpotCheckReferenceId referenceId = reportId.getReferenceId();
            observation.setReferenceId(referenceId);
            observation.setObservedDateTime(LocalDateTime.now());
            numOfMismatches[0] += observation.getMismatches().size();
            report.addObservation(observation);
        }
        logger.info("Found total number of "+numOfMismatches[0] +" mismatches");
        logger.info("Fetching Bill from Openleg xml-data-processing branch by iterating BaseBillId of BillView from Openleg Dev");
        return report;
    }

}
