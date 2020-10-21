package gov.nysenate.openleg.legislation.calendar.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplemental;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplementalId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class CalendarDataServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CalendarDataServiceTest.class);
    @Autowired
    private CalendarDataService calendarDataService;

    @Test
    public void getCalendarsByYearTest() {
        logger.info("Calendar Ids for 2014:");
        calendarDataService.getCalendars(2014, SortOrder.DESC, LimitOffset.ALL)
                .forEach(calId -> logger.info(calId.toString()));
    }

    @Test
    public void getCalendarTest() {
        CalendarSupplementalId calSupId = new CalendarSupplementalId(12, 2015, Version.ORIGINAL);
        CalendarSupplemental calendarSupplemental = calendarDataService.getCalendarSupplemental(calSupId);
        logger.info("got it.");
    }
}
