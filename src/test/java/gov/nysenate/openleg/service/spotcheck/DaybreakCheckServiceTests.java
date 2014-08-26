package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.dao.spotcheck.BaseBillIdSpotCheckReportDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.daybreak.DaybreakBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.bill.BillDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DaybreakCheckServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckServiceTests.class);

    @Autowired
    SpotCheckService<BaseBillId, Bill, DaybreakBill> billSpotCheck;

    @Autowired
    DaybreakCheckReportService daybreakReport;

    @Autowired
    BillDataService billData;

    @Autowired
    DaybreakDao daybreakDao;

    @Autowired
    private BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void testAutowired() throws Exception {
        assertNotNull(billSpotCheck);
    }

    @Test
    public void testCheck() throws Exception {
        List<BaseBillId> billIds = billData.getBillIds(SessionYear.current(), LimitOffset.FIFTY);
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.of(2014,8,25,12,0,0)));
        Map<BaseBillId, SpotCheckObservation<BaseBillId>> obsMap = new HashMap<>();
        for (BaseBillId billId : billIds) {
            DaybreakBillId daybreakId = new DaybreakBillId(billId, LocalDate.of(2014, 8, 22));
            DaybreakBill daybreakBill = daybreakDao.getDaybreakBill(daybreakId);
            Bill bill = billData.getBill(billId);
            SpotCheckObservation<BaseBillId> obs = billSpotCheck.check(bill, daybreakBill);
            obsMap.put(obs.getKey(), obs);
        }
        report.setObservations(obsMap);

//        report.setObservations();
        reportDao.saveReport(report);

//        logger.info("{}", bill.getActiveAmendment().getCoSponsors());
    }

    @Test
    public void testPublishedVersionsString() throws Exception {
//        Bill S3852 = billData.getBill(new BillId("S4998", 2013));
//        S3852.getAmendmentMap().remove(Version.DEFAULT);
////        String pubString = billSpotCheck.publishedVersionsString(S3852);
//        logger.info("{}", pubString);
    }

    @Test
    public void testActionsListString() throws Exception {
//        logger.info("{}", checkService.actionsListString(billData.getBill(new BillId("S1234", 2013)).getActions()));
    }
}
