package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.daybreak.*;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.List;

public class DaybreakProcessServiceTests extends BaseTests {

    private static Logger logger = LoggerFactory.getLogger(DaybreakProcessServiceTests.class);

    @Autowired
    private DaybreakProcessService daybreakProcessService;

    @Autowired
    private DaybreakDao daybreakDao;

    private static final LocalDate testReportdate = LocalDate.of(2014, 7, 18);

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
        List<DaybreakBill> daybreakBills = daybreakDao.getDaybreakBills(testReportdate);
        logger.info("got " + daybreakBills.size() + " daybreak bills");
    }

    @Test
    public void getCurrentDaybreakBills(){
        List<DaybreakBill> daybreakBills = daybreakDao.getCurrentDaybreakBills();
        logger.info("got " + daybreakBills.size() + " daybreak bills");
    }

    @Test
    public void setPending(){
        daybreakDao.setPendingProcessing(testReportdate);
    }

    @Test
    public void getReportDate(){
        logger.info("Current report date: " + daybreakDao.getCurrentReportDate());
        logger.info("Current report date for day of test report " + testReportdate + ": "
                       + daybreakDao.getPreviousCurrentReportDate(testReportdate));
        try {
            logger.info("Current report date for day before test report " + testReportdate + ": "
                    + daybreakDao.getPreviousCurrentReportDate(testReportdate.minusDays(1)));
        }
        catch(EmptyResultDataAccessException ex){
            logger.info("No report found before " + testReportdate);
        }
    }
}
