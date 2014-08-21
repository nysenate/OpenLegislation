package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.DaybreakBill;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.service.bill.BillDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DaybreakCheckServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckServiceTests.class);

    @Autowired
    SpotCheckService<BillId, Bill, DaybreakBill> billSpotCheck;

    private DaybreakCheckService checkService = new DaybreakCheckService();

    @Autowired
    BillDataService billData;

    @Test
    public void testAutowired() throws Exception {
        assertNotNull(billSpotCheck);
    }

    @Test
    public void testCheck() throws Exception {
        DaybreakBill daybreakBill = new DaybreakBill();
        BaseBillId S1234 = new BaseBillId("S1234", 2013);
        daybreakBill.setBaseBillId(S1234);
        daybreakBill.setReportDate(LocalDate.now());
        daybreakBill.setActiveVersion(Version.DEFAULT);
        daybreakBill.setSponsor("PERKINS");
        daybreakBill.setCosponsors(Arrays.asList("HASSELL-THOMPSON", "KRUEGER", "SERRANO"));
        daybreakBill.setTitle("Creates the office of the taxpayer advocate");
        daybreakBill.setLawSection("Tax Law");
        daybreakBill.setLawCodeAndSummary("Add SS3014 & 3015, amd S170, Tax L Creates the office of the taxpayer advocate; directs such office be in the control of the department of taxation and finance; outlines functions and duties of such office; creates mandatory reporting to the governor and legislative leaders.");
        List<BillAction> actions = Arrays.asList(
            new BillAction(LocalDate.of(2013, 1, 9), "REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS", Chamber.SENATE, 1, S1234),
            new BillAction(LocalDate.of(2014, 1, 8), "REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS", Chamber.SENATE, 2, S1234));

        Bill billS1234 = billData.getBill(S1234);
        logger.info("{}", checkService.check(billS1234));

    }

    @Test
    public void testPublishedVersionsString() throws Exception {
        Bill S3852 = billData.getBill(new BillId("S4998", 2013));
        S3852.getAmendmentMap().remove(Version.DEFAULT);
        String pubString = checkService.publishedVersionsString(S3852);
        logger.info("{}", pubString);
    }

    @Test
    public void testActionsListString() throws Exception {
        logger.info("{}", checkService.actionsListString(billData.getBill(new BillId("S1234", 2013)).getActions()));
    }
}
