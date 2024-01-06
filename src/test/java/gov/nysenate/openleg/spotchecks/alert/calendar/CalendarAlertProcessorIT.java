package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.processors.ParseError;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.legislation.bill.Version.ORIGINAL;
import static gov.nysenate.openleg.legislation.calendar.CalendarSectionType.*;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class CalendarAlertProcessorIT extends BaseTests {

    @Autowired
    private CalendarAlertProcessor process;

    private static CalendarAlertFile simpleFloor;
    private static CalendarAlertFile floorSupplemental;
    private static CalendarAlertFile simpleActiveList;
    private static CalendarAlertFile activeListSupplemental;

    @BeforeClass
    public static void classSetup() throws FileNotFoundException {
        simpleFloor = new CalendarAlertFile(
                FileIOUtils.getResourceFile("calendarAlerts/floor_cal_alert-2015-10-20150219T143033.html"));
        floorSupplemental = new CalendarAlertFile(
                FileIOUtils.getResourceFile("calendarAlerts/floor_cal_alert-2015-28B-20150331T185833.html"));
        simpleActiveList = new CalendarAlertFile(
                FileIOUtils.getResourceFile("calendarAlerts/active_list_alert-2015-10-20150224T193238.html"));
        activeListSupplemental = new CalendarAlertFile(
                FileIOUtils.getResourceFile("calendarAlerts/active_list_alert-2017-59-2-20170621T205235.html"));
    }

    @Test
    public void parsesCalendarId() throws ParseError {
        Calendar actualCalendar = process.process(simpleFloor);
        CalendarId expectedCalendarId = simpleCalendarId();
        assertEquals(expectedCalendarId, actualCalendar.getId());

        actualCalendar = process.process(floorSupplemental);
        expectedCalendarId = new CalendarId(28, 2015);
        assertEquals(expectedCalendarId, actualCalendar.getId());

        actualCalendar = process.process(activeListSupplemental);
        expectedCalendarId = new CalendarId(59, 2017);
        assertEquals(expectedCalendarId, actualCalendar.getId());
    }

    @Test
    public void parsesFloorSupplementalVersion() throws ParseError {
        Calendar actualCalendar = process.process(simpleFloor);
        assertEquals(1, actualCalendar.getSupplementalMap().size());
        assertNotNull(actualCalendar.getSupplemental(ORIGINAL));

        actualCalendar = process.process(floorSupplemental);
        assertEquals(1, actualCalendar.getSupplementalMap().size());
        assertNotNull(actualCalendar.getSupplemental(Version.B));

        // Active list alerts should have no floor supplementals.
        actualCalendar = process.process(activeListSupplemental);
        assertTrue(actualCalendar.getSupplementalMap().isEmpty());
        assertNull(actualCalendar.getSupplemental(ORIGINAL));
    }

    @Test
    public void parsesFloorSupplementalInfo() throws ParseError {
        CalendarId calendarId = simpleCalendarId();
        LocalDateTime releaseDateTime = LocalDateTime.of(2015, 2, 19, 14, 30, 33);
        LocalDate calendarDate = LocalDate.of(2015, 2, 25);
        CalendarSupplemental expectedSupplemental = new CalendarSupplemental(
                calendarId, ORIGINAL, calendarDate, releaseDateTime);

        Calendar actualCalendar = process.process(simpleFloor);
        CalendarSupplemental actualSupplemental = actualCalendar.getSupplemental(ORIGINAL);

        assertEquals(expectedSupplemental.getCalendarId(), actualSupplemental.getCalendarId());
        assertEquals(expectedSupplemental.getVersion(), actualSupplemental.getVersion());
        assertEquals(expectedSupplemental.getCalDate(), actualSupplemental.getCalDate());
        assertEquals(expectedSupplemental.getReleaseDateTime(), actualSupplemental.getReleaseDateTime());
    }

    @Test
    public void parsesEntries() {
        Calendar actualCalendar = process.process(simpleFloor);
        parseTypeHelper(actualCalendar, ORDER_OF_THE_FIRST_REPORT, 78, "S390");
        parseTypeHelper(actualCalendar, ORDER_OF_THE_SECOND_REPORT, 70, "S2236");
        parseTypeHelper(actualCalendar, THIRD_READING, 49, "S2314");
    }

    private static void parseTypeHelper(Calendar calendar, CalendarSectionType type, int billCalNo,
                                        String printNo) {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                billCalNo, type, new BillId(printNo, sessionYear), null, null);

        assertTrue(getSupplementalEntries(calendar, type).contains(entry));
    }

    private CalendarId simpleCalendarId() {
        return new CalendarId(10, 2015);
    }

    private static List<CalendarSupplementalEntry> getSupplementalEntries(Calendar actualCalendar,
                                                                   CalendarSectionType sectionType) {
        return actualCalendar.getSupplemental(Version.ORIGINAL).getSectionEntries().get(sectionType);
    }

    @Test
    public void parsesActiveListInfo() throws ParseError {
        CalendarId calendarId = new CalendarId(59, 2017);
        int sequenceNo = 2;
        String notes = "";
        LocalDateTime expectedReleaseDate = LocalDateTime.of(2017, 6, 21, 20, 52, 35);
        LocalDate expectedCalDate = LocalDate.of(2017, 6, 21);

        Calendar actualCalendar = process.process(activeListSupplemental);
        assertEquals(1, actualCalendar.getActiveListMap().size());
        var activeList  = actualCalendar.getActiveList(sequenceNo);
        assertEquals(calendarId, activeList.getCalendarId());
        assertEquals(sequenceNo, activeList.getSequenceNo().intValue());
        assertEquals(notes, activeList.getNotes());
        assertEquals(expectedCalDate, activeList.getCalDate());
        assertEquals(expectedReleaseDate, activeList.getReleaseDateTime());
    }

    @Test
    public void parsesActiveListEntries() throws ParseError {
        SessionYear sessionYear = SessionYear.of(simpleCalendarId().getYear());
        CalendarEntry entry = new CalendarEntry(46, new BillId("S2405", sessionYear));

        var actualCalendar = process.process(simpleActiveList).getActiveList(0).getEntries();
        assertTrue(actualCalendar.contains(entry));
        entry = new CalendarEntry(77, new BillId("S3407", sessionYear));
        assertTrue(actualCalendar.contains(entry));
    }
}
