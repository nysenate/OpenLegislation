package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.util.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActiveList extends BaseLegislativeContent
{
    /** A sequence number that identifies this active list. */
    private Integer sequenceNo;

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

    public CalendarActiveList(CalendarId calId, Integer sequenceNo, String notes, Date calDate, Date releaseDateTime) {
        this();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(DateHelper.resolveSession(getYear()));
        this.setSequenceNo(sequenceNo);
        this.setNotes(notes);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    /** --- Functional Getters/Setters --- */

    public void addEntry(CalendarActiveListEntry entry) {
        this.entries.add(entry);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarActiveList)) return false;
        if (!super.equals(o)) return false;
        CalendarActiveList that = (CalendarActiveList) o;
        if (calDate != null ? !calDate.equals(that.calDate) : that.calDate != null) return false;
        if (calendarId != null ? !calendarId.equals(that.calendarId) : that.calendarId != null) return false;
        if (entries != null ? !entries.equals(that.entries) : that.entries != null) return false;
        if (sequenceNo != null ? !sequenceNo.equals(that.sequenceNo) : that.sequenceNo != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (releaseDateTime != null ? !releaseDateTime.equals(that.releaseDateTime) : that.releaseDateTime != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sequenceNo != null ? sequenceNo.hashCode() : 0);
        result = 31 * result + (calendarId != null ? calendarId.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (calDate != null ? calDate.hashCode() : 0);
        result = 31 * result + (releaseDateTime != null ? releaseDateTime.hashCode() : 0);
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        return result;
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
