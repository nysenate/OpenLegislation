package gov.nysenate.openleg.model.spotcheck.calendar;

import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSectionType;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by kyle on 10/6/14.
 */
public class FloorCalendarSpotcheckReference {


    private LocalDateTime referenceDate;

    /** The identifier for this calendar supplemental. Typically a single character. */
    private Version version;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** The calendar date associated with this supplemental. */
    private LocalDate calDate;
    /** Mapping of supplemental entries to sections. */
    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries;


    public FloorCalendarSpotcheckReference(LocalDateTime referenceDate, Version version, CalendarId calendarId,
                                           LocalDate calDate, LinkedListMultimap<CalendarSectionType,
                                            CalendarSupplementalEntry> sectionEntries) {
        this.referenceDate = referenceDate;
        this.version = version;
        this.calendarId = calendarId;
        this.calDate = calDate;
        this.sectionEntries = sectionEntries;
    }


    public SpotCheckReferenceId getReferenceId() {
        return new SpotCheckReferenceId(SpotCheckRefType.LBDC_CALENDAR_ALERT, referenceDate);
    }

    public LocalDateTime getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDateTime referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    public void setCalDate(LocalDate calDate) {
        this.calDate = calDate;
    }

    public LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getSectionEntries() {
        return sectionEntries;
    }

    public void setSectionEntries(LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries) {
        this.sectionEntries = sectionEntries;
    }
}
