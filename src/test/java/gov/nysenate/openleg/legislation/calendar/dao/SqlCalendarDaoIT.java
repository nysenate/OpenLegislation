package gov.nysenate.openleg.legislation.calendar.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveListId;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.search.CalendarUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class SqlCalendarDaoIT extends BaseTests {

    @Autowired
    private SqlCalendarDao calendarDao;
    private int calSequenceNo = 0;

    @Test
    public void getCalendarTest() {
        Calendar cal = CalendarUtils.createGenericCalendar(new CalendarId(calSequenceNo, LocalDate.now().getYear()));
        putCalendar(cal);
        assertEquals(cal, calendarDao.getCalendar(cal.getId()));
    }

    @Test
    public void getActiveListTest() {
        CalendarId calId = new CalendarId(calSequenceNo, LocalDate.now().getYear());
        List<CalendarActiveList> singletonList = CalendarUtils.createActiveLists(5, 1, calId);
        Calendar cal = CalendarUtils.createCalendar(calId, singletonList, new HashSet<>());
        putCalendar(cal);
        CalendarActiveList getList = calendarDao.getActiveList(new CalendarActiveListId(calId, singletonList.get(0).getSequenceNo()));
        assertEquals(singletonList.get(0), getList);
    }

    @Test
    public void getActiveYearRangeTest() {
        try {
            calendarDao.getActiveYearRange();
        }
        catch (EmptyResultDataAccessException e) {
            assertEquals(0, calendarDao.getCalendarCount());
        }
        Calendar cal1 = CalendarUtils.createGenericCalendar(new CalendarId(calSequenceNo++, 2008));
        Calendar cal2 = CalendarUtils.createGenericCalendar(new CalendarId(calSequenceNo++, 2017));
        Calendar cal3 = CalendarUtils.createGenericCalendar(new CalendarId(calSequenceNo++, LocalDate.now().getYear() + 1));
        putCalendar(cal1);
        putCalendar(cal2);
        putCalendar(cal3);
        Range<Integer> range = calendarDao.getActiveYearRange();
        assertEquals(cal1.getYear(), (int)range.lowerEndpoint());
        assertEquals(cal3.getYear(), (int)range.upperEndpoint());
    }

    @Test
    public void getCountTest() {
        int initialCalCount = calendarDao.getCalendarCount();
        int numCals = 10;
        for (int i = 0; i < numCals; i++)
            putCalendar(CalendarUtils.createGenericCalendar(new CalendarId(calSequenceNo++, 2000)));
        assertEquals(initialCalCount + numCals, calendarDao.getCalendarCount());
    }

    @Test
    public void getIdsTest() {
        int testYear = LocalDate.now().getYear() + 1;
        int numCals = 5;
        for (int i = 0; i < numCals; i++)
            putCalendar(CalendarUtils.createGenericCalendar(new CalendarId(i, testYear)));
        List<CalendarId> ids = calendarDao.getCalendarIds(testYear, SortOrder.ASC, LimitOffset.ALL);
        for (int i = 0; i < numCals; i++) {
            CalendarId id = ids.get(i);
            assertEquals(testYear, id.getYear());
            assertEquals(id.getCalNo(), i);
        }
    }

    private void putCalendar(Calendar calendar) {
        calendarDao.updateCalendar(calendar, null);
    }
}