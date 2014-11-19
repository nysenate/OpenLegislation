package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.search.CalendarSearchDao;
import gov.nysenate.openleg.dao.calendar.search.ElasticCalendarSearchDao;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CalendarSearchDaoTests extends BaseTests
{

    private static final Logger logger = LoggerFactory.getLogger(CalendarSearchDaoTests.class);

    @Autowired
    ElasticCalendarSearchDao calendarSearchDao;

    @Autowired
    CalendarDataService calendarDataService;

    private static CalendarId testCalId = new CalendarId(54, 2014);

    @Test
    public void calendarIndexTest() {
        calendarSearchDao.updateCalendarIndex(calendarDataService.getCalendar(testCalId));
    }

    @Test
    public void deleteCalendarFromIndexTest() {
        calendarSearchDao.deleteCalendarFromIndex(testCalId);
    }

    @Test
    public void calendarBulkIndexTest() {
        calendarSearchDao.updateCalendarIndexBulk(calendarDataService.getCalendars(2014, SortOrder.NONE, LimitOffset.ALL));
    }

    @Test
    public void purgeIndexTest() {
        calendarSearchDao.purgeIndices();
    }

    @Test
    public void calendarSearchTest() {
//        SearchResults<CalendarId> results = calendarSearchDao.searchCalendars("\\*.basePrintNo: S2107", "", LimitOffset.ALL);
//        logger.info("results");
//        results.getResults().stream().map(r -> r.getResult().toString()).forEach(logger::info);
    }
}
