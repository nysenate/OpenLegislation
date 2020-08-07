package gov.nysenate.openleg.dao.calendar.search;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class CalendarSearchDaoTest extends BaseTests
{
    @Autowired
    ElasticCalendarSearchDao calendarSearchDao;

    @Autowired
    CalendarDataService calendarDataService;
}
