package gov.nysenate.openleg.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.bill.reference.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.List;

public class DaybreakProcessServiceTests extends BaseTests {

    private static Logger logger = LoggerFactory.getLogger(DaybreakProcessServiceTests.class);

    @Autowired
    private DaybreakProcessService daybreakProcessService;

    @Autowired
    private DaybreakDao daybreakDao;

    private static final LocalDate testReportdate = LocalDate.of(2014, 8, 8);

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
        Range<LocalDate> dateRange = Range.closed(LocalDate.of(2014, 8, 6), LocalDate.of(2014, 8, 8));
        List<DaybreakBill> daybreakBills = daybreakDao.getCurrentDaybreakBills(dateRange);
        logger.info("got " + daybreakBills.size() + " daybreak bills");
    }

    @Test
    public void setPending(){
        daybreakDao.setPendingProcessing(testReportdate);
    }

    @Test
    public void setAllNotPending(){
        daybreakDao.getAllReportDates().forEach(daybreakDao::setProcessed);
    }

    @Test
    public void setAllPending(){
        daybreakDao.getAllReportDates().forEach(daybreakDao::setPendingProcessing);
    }

    @Test
    public void getReportDate(){
        logger.info("Current report date: " + daybreakDao.getCurrentReportDate());
        logger.info("Current report date for day of test report " + testReportdate + ": "
                       + daybreakDao.getCurrentReportDate());
        try {
            logger.info("Current report date for day before test report " + testReportdate + ": "
                    + daybreakDao.getCurrentReportDate());
        }
        catch(EmptyResultDataAccessException ex){
            logger.info("No report found before " + testReportdate);
        }
    }
}
