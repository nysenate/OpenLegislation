package gov.nysenate.openleg.service.spotcheck;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.reference.daybreak.DaybreakDao;
import gov.nysenate.openleg.dao.spotcheck.BaseBillIdSpotCheckReportDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakReportService;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;

public class DaybreakCheckServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakCheckServiceTests.class);

    @Autowired
    SpotCheckService<BaseBillId, Bill, DaybreakBill> billSpotCheck;

    @Autowired
    DaybreakReportService daybreakReport;

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
        Range<LocalDate> dateRange = Range.closed(DateUtils.LONG_AGO, LocalDate.now());
        logger.info("{}", dateRange.lowerEndpoint());
        logger.info("{}", dateRange.upperEndpoint());

        Bill bill = billData.getBill(new BaseBillId("S6671", 2013));
        logger.info("{}", OutputUtils.toJson(billSpotCheck.check(bill)));
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
