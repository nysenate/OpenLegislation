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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class CalendarAlertParserTest extends BaseTests {

    @Autowired
    private CalendarAlertParser parser;

    private static final File simpleAlertFile = new File("/data/cal_alerts/floor_cal_alert-2015-10-20150219T143033.html");
    private static final File alertFile = new File("/data/cal_alerts/floor_cal_alert-2015-28B-20150331T185833.html");

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
    public void parsesOrderOfFirstReportSupplementalEntries() {
        SessionYear sessionYear = SessionYear.of(2015);
        CalendarSectionType sectionType = CalendarSectionType.ORDER_OF_THE_FIRST_REPORT;
        CalendarSupplementalEntry entry = new CalendarSupplementalEntry(
                78, sectionType, new BillId("S390", sessionYear), null, null);

        Calendar actualCalendar = parser.parse(simpleAlertFile);
        assertThat(getSupplementalEntries(actualCalendar, Version.DEFAULT, sectionType), hasItem(entry));
    }

    private List<CalendarSupplementalEntry> getSupplementalEntries(Calendar actualCalendar, Version version, CalendarSectionType sectionType) {
        return actualCalendar.getSupplemental(version).getSectionEntries().get(sectionType);
    }

    private CalendarId simpleCalendarId() {
        return new CalendarId(10, 2015);
    }

    // matches File: /data/floor_cal_alert-2015-10A-20150225T121900
    private Calendar createSimpleTestCalendar() {
        SessionYear session = SessionYear.of(2015);

        CalendarId calId = new CalendarId(10, 2015);
        Calendar expectedCalendar = new Calendar(calId);
        CalendarSupplemental supplemental = new CalendarSupplemental(
                calId, Version.A, LocalDate.of(2015, 2, 25), LocalDateTime.of(2015, 2, 25, 12, 19, 0));

        CalendarSupplementalEntry supplementalEntry = new CalendarSupplementalEntry(
                78, CalendarSectionType.ORDER_OF_THE_FIRST_REPORT, new BillId("S290", session), null, false);
        supplemental.addEntry(supplementalEntry);

        supplementalEntry = new CalendarSupplementalEntry(
                68, CalendarSectionType.ORDER_OF_THE_SECOND_REPORT, new BillId("S456", session), null, false);
        supplemental.addEntry(supplementalEntry);

        supplementalEntry = new CalendarSupplementalEntry(
                11, CalendarSectionType.THIRD_READING, new BillId("S1314A", session), null, false);
        supplemental.addEntry(supplementalEntry);

        expectedCalendar.putSupplemental(supplemental);

        return expectedCalendar;
    }
}
