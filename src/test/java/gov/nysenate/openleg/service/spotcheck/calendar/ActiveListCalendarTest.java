package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by senateuser on 5/24/2017.
 */
public class ActiveListCalendarTest extends BaseTests {
    private final File alertFile = new File(getClass().getClassLoader().getResource("calendarAlerts/active_list_alert-2015-10-20150224T193238.html").getFile());

    @Autowired
    private CalendarAlertProcessor process;
    @Autowired
    private CalendarCheckService calendarCheckService;

    @Test
    public void activeListCalendarTest() {
        Calendar dummyCalendar = new Calendar(new CalendarEntryListId(new CalendarId(10, 2015), CalendarType.FLOOR_CALENDAR, Version.DEFAULT, 0));
        Calendar expected = process.process(alertFile);
        List<SpotCheckObservation<CalendarEntryListId>> spotCheckObservation = calendarCheckService.checkAll(dummyCalendar,expected);
        CalendarEntryListId actual = spotCheckObservation.get(0).getKey();
        assertTrue(actual.getType().equals(CalendarType.ACTIVE_LIST));
        assertEquals(expected.getId().getCalNo(),actual.getCalendarId().getCalNo());
        assertEquals(expected.getId().getYear(),actual.getCalendarId().getYear());

        System.out.println(spotCheckObservation);
    }

}
