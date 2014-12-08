package gov.nysenate.openleg.view;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.client.view.calendar.CalendarSupView;
import gov.nysenate.openleg.client.view.calendar.CalendarViewFactory;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarSupplemental;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(ViewTests.class);

    @Autowired
    CalendarDataService calendarDataService;

    @Autowired
    CalendarViewFactory calendarViewFactory;

    @Test
    public void calSupEntryViewTest() {
        CalendarSupplemental calSup = calendarDataService.getCalendarSupplemental(new CalendarSupplementalId(45, 2014, Version.of("")));
        CalendarSupView calSupView = calendarViewFactory.getCalendarSupView(calSup);
    }
}
