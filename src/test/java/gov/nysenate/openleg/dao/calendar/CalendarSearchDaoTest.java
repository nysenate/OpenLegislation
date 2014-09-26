package gov.nysenate.openleg.dao.calendar;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Range;
import com.google.common.collect.SetMultimap;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.service.calendar.search.CalendarSearchParameters;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

public class CalendarSearchDaoTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(CalendarSearchDaoTest.class);

    @Autowired
    private CalendarSearchDao calendarSearchDao;

    private CalendarSearchParameters testParams;
    private CalendarSearchParameters testParams2;

    @PostConstruct
    private void init() {
        testParams = new CalendarSearchParameters();
        testParams.setCalendarType(CalendarType.ALL);
        testParams.setYear(2014);
        testParams.setDateRange(Range.closed(LocalDate.of(2014, 6, 5), LocalDate.of(2014, 9, 23)));
        SetMultimap<Integer, BillId> printNoFilter = HashMultimap.create();
        printNoFilter.put(1, new BillId("S6738B", 2013));
        printNoFilter.put(1, new BillId("S726A", 2013));
        printNoFilter.put(2, new BillId("S726A", 2013));
        printNoFilter.put(2, new BillId("S7842", 2013));
        testParams.setBillPrintNo(printNoFilter);
        SetMultimap<Integer, Integer> bcnFilter = HashMultimap.create();
        bcnFilter.put(1, 698);
        bcnFilter.put(1, 1160);
        bcnFilter.put(2, 1160);
        bcnFilter.put(2, 1627);
        testParams.setBillCalendarNo(bcnFilter);

        testParams2 = new CalendarSearchParameters();
        testParams2.setCalendarType(CalendarType.FLOOR);
        testParams2.setYear(2014);
        testParams2.setDateRange(Range.closed(LocalDate.of(2014, 6, 5), LocalDate.of(2014, 9, 23)));
        SetMultimap<Integer, Integer> sCodeFilter = HashMultimap.create();
        sCodeFilter.put(1, 400);
    }

    @Test
    public void queryBothTest() {
        List<CalendarId> calIds = calendarSearchDao.getCalendars(testParams, new OrderBy("calendar_no", SortOrder.ASC), null);
        logger.info("Results: ");
        calIds.forEach(calId -> logger.info(calId.toString()));
    }

    @Test
    public void queryActiveListTest() {
        testParams.setCalendarType(CalendarType.ACTIVE_LIST);
        List<CalendarActiveListId> calIds = calendarSearchDao.getActiveLists(testParams, new OrderBy("calendar_no", SortOrder.ASC), null);
        logger.info("Results: ");
        calIds.forEach(calId -> logger.info(calId.toString()));
    }

    @Test
    public void queryFloorTest() {
        List<CalendarSupplementalId> calIds = calendarSearchDao.getFloorCalendars(testParams2, new OrderBy("calendar_no", SortOrder.ASC), null);
        logger.info("Results: " + calIds.size());
        calIds.forEach(calId -> logger.info(calId.toString()));
    }

    @Test
    public void getQueryCountTest() {
        logger.info("Count: " + calendarSearchDao.getCalendarCountforQuery(testParams2));
    }
}
