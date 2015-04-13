package gov.nysenate.openleg.processor.calendar;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.processor.spotcheck.calendar.CalendarAlertParser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CalendarAlertParserTest extends BaseTests {

    @Autowired
    private CalendarAlertParser parser;

    private final File simpleAlertFile = new File(getClass().getClassLoader().getResource("calendarAlerts/floor_cal_alert-2015-10-20150219T143033.html").getFile());
    private final File alertFile = new File(getClass().getClassLoader().getResource("calendarAlerts/floor_cal_alert-2015-28B-20150331T185833.html").getFile());
    private final File simpleActiveListFile = new File(getClass().getClassLoader().getResource("calendarAlerts/active_list_alert-2015-10-20150224T193238.html").getFile());

    @Test
    public void parsesSimpleCalendarIdInfo() {
        Calendar actualCalendar = parser.parse(simpleAlertFile);
        CalendarId expectedCalendarId = simpleCalendarId();
        assertThat(actualCalendar.getId(), is(expectedCalendarId));
    }

    @Test
    public void parsesCalendarIdInfoWhenVersionPresent() {
        Calendar actualCalendar = parser.parse(alertFile);
        CalendarId expectedId = new CalendarId(28, 2015);
        assertThat(actualCalendar.getId(), is(expectedId));
    }

    @Test
    public void parsesSupplementalVersion() {
        Calendar actualCalendar = parser.parse(simpleAlertFile);
        assertThat(actualCalendar.getSupplementalMap().keySet().size(), is(1));
        assertThat(actualCalendar.getSupplemental(Version.DEFAULT), is(not(nullValue())));

        actualCalendar = parser.parse(alertFile);
        assertThat(actualCalendar.getSupplementalMap().keySet().size(), is(1));
        assertThat(actualCalendar.getSupplemental(Version.B), is(not(nullValue())));
    }

    @Test
    public void parsesSupplementalInfo() {
        CalendarId calendarId = simpleCalendarId();
        LocalDateTime dateTime = LocalDateTime.of(2015, 2, 19, 14, 30, 33);
        CalendarSupplemental expectedSupplemental = new CalendarSupplemental(
                calendarId, Version.DEFAULT, dateTime.toLocalDate(), dateTime);

        Calendar actualCalendar = parser.parse(simpleAlertFile);
        CalendarSupplemental actualSupplemental = actualCalendar.getSupplemental(Version.DEFAULT);

        assertThat(actualSupplemental.getCalendarId(), is(expectedSupplemental.getCalendarId()));
        assertThat(actualSupplemental.getVersion(), is(expectedSupplemental.getVersion()));
        assertThat(actualSupplemental.getCalDate(), is(expectedSupplemental.getCalDate()));
        assertThat(actualSupplemental.getReleaseDateTime(), is(expectedSupplemental.getReleaseDateTime()));
    }

    @Test
    public void parsesOrderOfFirstReportEntries() {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.ORDER_OF_THE_FIRST_REPORT;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                78, sectionType, new BillId("S390", sessionYear), null, null);

        Calendar actualCalendar = parser.parse(simpleAlertFile);
        assertThat(getSupplementalEntries(actualCalendar, Version.DEFAULT, sectionType), hasItem(entry));
    }

    @Test
    public void parsesOrderOfSecondReportEntries() {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.ORDER_OF_THE_SECOND_REPORT;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                70, sectionType, new BillId("S2236", sessionYear), null, null);

        Calendar actualCalendar = parser.parse(simpleAlertFile);
        assertThat(getSupplementalEntries(actualCalendar, Version.DEFAULT, sectionType), hasItem(entry));
    }

    @Test
    public void parsesOnThirdReadingEntries() {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.THIRD_READING;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                49, sectionType, new BillId("S2314", sessionYear), null, null);

        Calendar actualCalendar = parser.parse(simpleAlertFile);
        assertThat(getSupplementalEntries(actualCalendar, Version.DEFAULT, sectionType), hasItem(entry));
    }

    private CalendarId simpleCalendarId() {
        return new CalendarId(10, 2015);
    }

    private List<CalendarSupplementalEntry> getSupplementalEntries(Calendar actualCalendar, Version version, CalendarSectionType sectionType) {
        return actualCalendar.getSupplemental(version).getSectionEntries().get(sectionType);
    }

    @Test
    public void parsesActiveListInfo() {
        CalendarId calendarId = simpleCalendarId();
        int sequenceNo = 0;
        String notes = "";
        LocalDateTime expectedReleaseDate = LocalDateTime.of(2015, 2, 24, 19, 32, 38);
        LocalDate expectedCalDate = expectedReleaseDate.toLocalDate();

        Calendar actualCalendar = parser.parse(simpleActiveListFile);
        assertThat(actualCalendar.getActiveListMap().size(), is(1));
        assertThat(actualCalendar.getActiveList(0), is(notNullValue()));
        assertThat(actualCalendar.getActiveList(0).getCalendarId(), is(calendarId));
        assertThat(actualCalendar.getActiveList(0).getSequenceNo(), is(sequenceNo));
        assertThat(actualCalendar.getActiveList(0).getNotes(), is(notes));
        assertThat(actualCalendar.getActiveList(0).getCalDate(), is(expectedCalDate));
        assertThat(actualCalendar.getActiveList(0).getReleaseDateTime(), is(expectedReleaseDate));
    }

    // TODO: test sequence number generation

    @Test
    public void parsesActiveListEntries() {
        CalendarId id = simpleCalendarId();
        SessionYear sessionYear = SessionYear.of(id.getYear());
        CalendarActiveListEntry entry = new CalendarActiveListEntry(46, new BillId("S2405", sessionYear ));

        Calendar actualCalendar = parser.parse(simpleActiveListFile);
        assertThat(actualCalendar.getActiveList(0).getEntries(), hasItem(entry));

        entry = new CalendarActiveListEntry(77, new BillId("S3407", sessionYear ));
        assertThat(actualCalendar.getActiveList(0).getEntries(), hasItem(entry));
    }
}
