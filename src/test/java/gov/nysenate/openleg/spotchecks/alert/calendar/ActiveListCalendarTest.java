package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.CalendarType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by senateuser on 5/24/2017.
 */
public class ActiveListCalendarTest extends BaseTests {
    private final File alertFile = FileIOUtils.getResourceFile("calendarAlerts/active_list_alert-2015-10-20150224T193238.html");

    @Autowired
    private CalendarAlertProcessor process;
    @Autowired
    private CalendarCheckService calendarCheckService;

    @Test
    public void activeListCalendarTest() throws FileNotFoundException {
        Calendar dummyCalendar = new Calendar(new CalendarEntryListId(new CalendarId(10, 2015), CalendarType.FLOOR_CALENDAR, Version.ORIGINAL, 0));
        Calendar expected = process.process(new CalendarAlertFile(alertFile));
        List<SpotCheckObservation<CalendarEntryListId>> spotCheckObservation = calendarCheckService.checkAll(dummyCalendar,expected);
        CalendarEntryListId actual = spotCheckObservation.get(0).getKey();
        assertTrue(actual.getType().equals(CalendarType.ACTIVE_LIST));
        assertEquals(expected.getId().getCalNo(),actual.getCalendarId().getCalNo());
        assertEquals(expected.getId().getYear(),actual.getCalendarId().getYear());

        System.out.println(spotCheckObservation);
    }

}
