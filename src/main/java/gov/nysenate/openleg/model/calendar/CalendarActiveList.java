package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.BaseLegislativeContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActiveList extends BaseLegislativeContent
{
    /** A sequence number that identifies this active list. */
    private Integer id;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** Any notes pertaining to this active list. */
    private String notes;

    /** The calendar date associated with this supplemental. */
    private Date calDate;

    /** The date time this active list was released. */
    private Date releaseDateTime;

    /** Active list entries. */
    private List<CalendarActiveListEntry> entries;

    /** --- Constructors --- */

    public CalendarActiveList() {
        super();
        this.setEntries(new ArrayList<CalendarActiveListEntry>());
    }

    public CalendarActiveList(CalendarId calId, Integer id, String notes, Date calDate, Date releaseDateTime) {
        this();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(resolveSessionYear(getYear()));
        this.setId(id);
        this.setNotes(notes);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addEntry(CalendarActiveListEntry entry) {
        this.entries.add(entry);
    }

    /** --- Basic Getters/Setters --- */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCalDate() {
        return calDate;
    }

    public void setCalDate(Date calDate) {
        this.calDate = calDate;
    }

    public Date getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(Date releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public List<CalendarActiveListEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CalendarActiveListEntry> entries) {
        this.entries = entries;
    }
}
