package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckService;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class DaybreakSpotCheckReportDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportDaoTests.class);

    @Autowired
    DaybreakCheckService daybreakCheckService;

    @Autowired
    BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void testGetReport() throws Exception {
        LocalDateTime reportDateTime = LocalDateTime.of(2014, 8, 25, 15, 57, 51, 783000000);
        SpotCheckReport<BaseBillId> report = reportDao.getReport(
            new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, reportDateTime));
        logger.info("{}", OutputUtils.toJson(report));
    }

    @Test
    public void getOpenObsTest() {
        //Map<BaseBillId, SpotCheckObservation<BaseBillId>> obs = reportDao.getOpenObservations(SpotCheckRefType.LBDC_SCRAPED_BILL);
        logger.info("hi");
    }

    @Test
    public void testReportIds() throws Exception {
        logger.info("{}", reportDao.getReportSummaries(SpotCheckRefType.LBDC_DAYBREAK, LocalDateTime.of(2014, 1, 1, 0, 0, 0), LocalDateTime.now(), SortOrder.DESC));
    }

    @Test
    public void testEndToEnd() throws Exception {


    }
}
