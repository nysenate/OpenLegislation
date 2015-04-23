package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalendarActiveList extends BaseLegislativeContent
{
    /** A sequence number that identifies this active list. */
    private Integer sequenceNo;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** Any notes pertaining to this active list. */
    private String notes;

    /** The calendar date associated with this supplemental. */
    private LocalDate calDate;

    /** The date time this active list was released. */
    private LocalDateTime releaseDateTime;

    /** Active list entries. */
    private List<CalendarEntry> entries;

    /** --- Constructors --- */

    public CalendarActiveList() {
        super();
        this.setEntries(new ArrayList<>());
    }

    public CalendarActiveList(CalendarId calId, Integer sequenceNo, String notes, LocalDate calDate,
                              LocalDateTime releaseDateTime) {
        this();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(new SessionYear(getYear()));
        this.setSequenceNo(sequenceNo);
        this.setNotes(notes);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addEntry(CalendarEntry entry) {
        this.entries.add(entry);
    }


    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CalendarActiveList other = (CalendarActiveList) obj;
        return Objects.equals(this.sequenceNo, other.sequenceNo) &&
               Objects.equals(this.calendarId, other.calendarId) &&
               Objects.equals(this.notes, other.notes) &&
               Objects.equals(this.calDate, other.calDate) &&
               Objects.equals(this.releaseDateTime, other.releaseDateTime) &&
               Objects.equals(this.entries, other.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenceNo, calendarId, notes, calDate, releaseDateTime, entries);
    }

    /** --- Functional Getters/Setters --- */

    public CalendarActiveListId getCalendarActiveListId() {
        return new CalendarActiveListId(calendarId, sequenceNo);
    }

    /** --- Basic Getters/Setters --- */

    public Integer getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    public void setCalDate(LocalDate calDate) {
        this.calDate = calDate;
    }

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(LocalDateTime releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public List<CalendarEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CalendarEntry> entries) {
        this.entries = entries;
    }
}
