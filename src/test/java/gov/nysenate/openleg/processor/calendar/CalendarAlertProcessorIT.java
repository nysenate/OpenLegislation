package gov.nysenate.openleg.processor.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertProcessor;
import gov.nysenate.openleg.util.FileIOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@Category(IntegrationTest.class)
public class CalendarAlertProcessorIT extends BaseTests {

    @Autowired
    private CalendarAlertProcessor process;

    private static CalendarAlertFile simpleFloor;
    private static CalendarAlertFile floorSupplemental;
    private static CalendarAlertFile simpleActiveList;
    private static CalendarAlertFile activeListSupplemental;

    @BeforeClass
    public static void beforeclass() throws FileNotFoundException {
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
        assertThat(actualCalendar.getId(), is(expectedCalendarId));

        actualCalendar = process.process(floorSupplemental);
        expectedCalendarId = new CalendarId(28, 2015);
        assertThat(actualCalendar.getId(), is(expectedCalendarId));

        actualCalendar = process.process(activeListSupplemental);
        expectedCalendarId = new CalendarId(59, 2017);
        assertThat(actualCalendar.getId(), is(expectedCalendarId));
    }

    @Test
    public void parsesFloorSupplementalVersion() throws ParseError {
        Calendar actualCalendar = process.process(simpleFloor);
        assertThat(actualCalendar.getSupplementalMap().keySet().size(), is(1));
        assertThat(actualCalendar.getSupplemental(Version.ORIGINAL), is(not(nullValue())));

        actualCalendar = process.process(floorSupplemental);
        assertThat(actualCalendar.getSupplementalMap().keySet().size(), is(1));
        assertThat(actualCalendar.getSupplemental(Version.B), is(not(nullValue())));

        // Active list alerts should have no floor supplemental's.
        actualCalendar = process.process(activeListSupplemental);
        assertThat(actualCalendar.getSupplementalMap().keySet().size(), is(0));
        assertThat(actualCalendar.getSupplemental(Version.ORIGINAL), is(nullValue()));
    }

    @Test
    public void parsesFloorSupplementalInfo() throws ParseError {
        CalendarId calendarId = simpleCalendarId();
        LocalDateTime releaseDateTime = LocalDateTime.of(2015, 2, 19, 14, 30, 33);
        LocalDate calendarDate = LocalDate.of(2015, 2, 25);
        CalendarSupplemental expectedSupplemental = new CalendarSupplemental(
                calendarId, Version.ORIGINAL, calendarDate, releaseDateTime);

        Calendar actualCalendar = process.process(simpleFloor);
        CalendarSupplemental actualSupplemental = actualCalendar.getSupplemental(Version.ORIGINAL);

        assertThat(actualSupplemental.getCalendarId(), is(expectedSupplemental.getCalendarId()));
        assertThat(actualSupplemental.getVersion(), is(expectedSupplemental.getVersion()));
        assertThat(actualSupplemental.getCalDate(), is(expectedSupplemental.getCalDate()));
        assertThat(actualSupplemental.getReleaseDateTime(), is(expectedSupplemental.getReleaseDateTime()));
    }

    @Test
    public void parsesOrderOfFirstReportEntries() throws ParseError {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.ORDER_OF_THE_FIRST_REPORT;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                78, sectionType, new BillId("S390", sessionYear), null, null);

        Calendar actualCalendar = process.process(simpleFloor);
        assertThat(getSupplementalEntries(actualCalendar, Version.ORIGINAL, sectionType), hasItem(entry));
    }

    @Test
    public void parsesOrderOfSecondReportEntries() throws ParseError {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.ORDER_OF_THE_SECOND_REPORT;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                70, sectionType, new BillId("S2236", sessionYear), null, null);

        Calendar actualCalendar = process.process(simpleFloor);
        assertThat(getSupplementalEntries(actualCalendar, Version.ORIGINAL, sectionType), hasItem(entry));
    }

    @Test
    public void parsesOnThirdReadingEntries() throws ParseError {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.THIRD_READING;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                49, sectionType, new BillId("S2314", sessionYear), null, null);

        Calendar actualCalendar = process.process(simpleFloor);
        assertThat(getSupplementalEntries(actualCalendar, Version.ORIGINAL, sectionType), hasItem(entry));
    }

    private CalendarId simpleCalendarId() {
        return new CalendarId(10, 2015);
    }

    private List<CalendarSupplementalEntry> getSupplementalEntries(Calendar actualCalendar, Version version, CalendarSectionType sectionType) {
        return actualCalendar.getSupplemental(version).getSectionEntries().get(sectionType);
    }

    @Test
    public void parsesActiveListInfo() throws ParseError {
        CalendarId calendarId = new CalendarId(59, 2017);
        int sequenceNo = 2;
        String notes = "";
        LocalDateTime expectedReleaseDate = LocalDateTime.of(2017, 6, 21, 20, 52, 35);
        LocalDate expectedCalDate = LocalDate.of(2017, 6, 21);

        Calendar actualCalendar = process.process(activeListSupplemental);
        assertThat(actualCalendar.getActiveListMap().size(), is(1));
        assertThat(actualCalendar.getActiveList(sequenceNo).getCalendarId(), is(calendarId));
        assertThat(actualCalendar.getActiveList(sequenceNo).getSequenceNo(), is(sequenceNo));
        assertThat(actualCalendar.getActiveList(sequenceNo).getNotes(), is(notes));
        assertThat(actualCalendar.getActiveList(sequenceNo).getCalDate(), is(expectedCalDate));
        assertThat(actualCalendar.getActiveList(sequenceNo).getReleaseDateTime(), is(expectedReleaseDate));
    }

    @Test
    public void parsesActiveListEntries() throws ParseError {
        CalendarId id = simpleCalendarId();
        SessionYear sessionYear = SessionYear.of(id.getYear());
        CalendarEntry entry = new CalendarEntry(46, new BillId("S2405", sessionYear ));

        Calendar actualCalendar = process.process(simpleActiveList);
        assertThat(actualCalendar.getActiveList(0).getEntries(), hasItem(entry));

        entry = new CalendarEntry(77, new BillId("S3407", sessionYear ));
        assertThat(actualCalendar.getActiveList(0).getEntries(), hasItem(entry));
    }
}
