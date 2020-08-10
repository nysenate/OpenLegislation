package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import gov.nysenate.openleg.util.FileIOUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by senateuser on 5/24/2017.
 */
@Category(IntegrationTest.class)
public class CalendarCheckServiceIT extends BaseTests {
    private final File activeAlertFile = FileIOUtils.getResourceFile("calendarAlerts/active_list_alert-2015-10-20150224T193238.html");
    private final File suppAlertFile = FileIOUtils.getResourceFile("calendarAlerts/floor_cal_alert-2015-28B-20150331T185833.html");
    private final File floorAlertFile = FileIOUtils.getResourceFile("calendarAlerts/floor_cal_alert-2015-10-20150219T143033.html");

    @Autowired
    private CalendarAlertProcessor processor;
    @Autowired
    private CalendarCheckService calendarCheckService;

    @Test
    public void activeListCalendarTest() throws FileNotFoundException {
        calendarTest(CalendarType.ACTIVE_LIST, 10, activeAlertFile);
    }

    @Test
    public void supplementalCalendarTest() throws FileNotFoundException {
        calendarTest(CalendarType.SUPPLEMENTAL_CALENDAR, 28, suppAlertFile);
    }

    @Test
    public void floorCalendarTest() throws FileNotFoundException {
        calendarTest(CalendarType.FLOOR_CALENDAR, 10, floorAlertFile);
    }

    private void calendarTest(CalendarType type, int calNo, File file) throws FileNotFoundException {
        Calendar dummyCalendar = new Calendar(new CalendarEntryListId(new CalendarId(calNo, 2015), type, Version.ORIGINAL, 0));
        Calendar expected = processor.process(new CalendarAlertFile(file));
        List<SpotCheckObservation<CalendarEntryListId>> spotCheckObservation = calendarCheckService.checkAll(dummyCalendar, expected);
        CalendarEntryListId actual = spotCheckObservation.get(0).getKey();
        assertEquals(type, actual.getType());
        assertEquals(expected.getId().getCalNo(),actual.getCalendarId().getCalNo());
        assertEquals(expected.getId().getYear(),actual.getCalendarId().getYear());
    }
}
