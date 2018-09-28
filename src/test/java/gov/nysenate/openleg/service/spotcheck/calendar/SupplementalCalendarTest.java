package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.calendar.data.CalendarDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Chenguang He on 5/24/2017.
 */
public class SupplementalCalendarTest extends BaseTests {

    private final File alertFile = new File(getClass().getClassLoader().getResource("calendarAlerts/floor_cal_alert-2015-28B-20150331T185833.html").getFile());

    @Autowired
    private CalendarAlertProcessor process;
    @Autowired
    private CalendarCheckService calendarCheckService;

    @Test
    public void supplementalCalendarTest() throws FileNotFoundException {
        Calendar dummyCalendar = new Calendar(new CalendarEntryListId(new CalendarId(28, 2015), CalendarType.FLOOR_CALENDAR, Version.ORIGINAL, 0));
        Calendar expected = process.process(new CalendarAlertFile(alertFile));
       List<SpotCheckObservation<CalendarEntryListId>> spotCheckObservation = calendarCheckService.checkAll(dummyCalendar,expected);
       CalendarEntryListId actual = spotCheckObservation.get(0).getKey();
       assertTrue(actual.getType().equals(CalendarType.FLOOR_CALENDAR));
       assertEquals(expected.getId().getCalNo(),actual.getCalendarId().getCalNo());
        assertEquals(expected.getId().getYear(),actual.getCalendarId().getYear());

        System.out.println(spotCheckObservation);
    }
}
