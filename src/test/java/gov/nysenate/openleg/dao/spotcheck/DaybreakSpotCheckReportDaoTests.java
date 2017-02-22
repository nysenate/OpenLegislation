package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.daybreak.DaybreakCheckService;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DaybreakSpotCheckReportDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotCheckReportDaoTests.class);

    @Autowired
    DaybreakCheckService daybreakCheckService;

    @Autowired
    BaseBillIdSpotCheckReportDao reportDao;

    @Test
    public void setIgnoreStatusTest() {
        //Shouldn't do anything
        reportDao.setMismatchIgnoreStatus(-1, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        // Should set ignore status
        reportDao.setMismatchIgnoreStatus(5235, SpotCheckMismatchIgnore.IGNORE_PERMANENTLY);
        // Should delete ignore status
        reportDao.setMismatchIgnoreStatus(5235, null);
    }

    @Test
    public void getOpenObsTest() {
        //Map<BaseBillId, SpotCheckObservation<BaseBillId>> obs = reportDao.getOpenMismatches(SpotCheckRefType.LBDC_SCRAPED_BILL);
        logger.info("hi");
    }

    @Test
    public void testEndToEnd() throws Exception {


    }
}
