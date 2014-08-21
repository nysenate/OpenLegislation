package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.daybreak.*;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class DaybreakProcessServiceTests extends BaseTests {

    private static Logger logger = LoggerFactory.getLogger(DaybreakProcessServiceTests.class);

    @Autowired
    private DaybreakProcessService daybreakProcessService;

    @Autowired
    private DaybreakDao daybreakDao;

    @Test
    public void stageDaybreakFileTest(){
        daybreakProcessService.collateDaybreakReports();
    }

    @Test
    public void processPendingFragmentsTest(){
        daybreakProcessService.processPendingFragments();
    }

    @Test
    public void getDaybreakBills(){
        List<DaybreakBill> daybreakBills = daybreakDao.getDaybreakBills(LocalDate.of(2014, 7, 18));
        logger.info("got " + daybreakBills.size() + " daybreak bills");
    }
}
