package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;

import java.util.Date;
import java.util.List;

public class CalendarSupplemental extends BaseLegislativeContent
{
    /** The identifier for this calendar supplemental. Typically a single character. */
    private String version;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** The calendar date associated with this supplemental. */
    private Date calDate;

    /** The date when this supplemental was released. */
    private Date releaseDateTime;

    /** Mapping of supplemental entries to sections. */
    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries;

    /** --- Constructors --- */

    public CalendarSupplemental(CalendarId calId, String version, Date calDate, Date releaseDateTime) {
        this.sectionEntries = LinkedListMultimap.create();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(resolveSessionYear(getYear()));
        this.setVersion(version);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Adds a CalendarSupplementalEntry to this supplemental.
     *
     * @param entry CalendarSupplementalEntry
     */
    public void addEntry(CalendarSupplementalEntry entry) {
        if (entry != null) {
            if (entry.getSectionType() != null) {
                sectionEntries.put(entry.getSectionType(), entry);
            }
            else {
                throw new IllegalArgumentException("CalendarSupplementalEntry cannot have a null section type.");
            }
        }
        else {
            throw new IllegalArgumentException("CalendarSupplementalEntry cannot be null.");
        }
    }

    /**
     * Retrieves a list of CalendarSupplementalEntry of the given sectionType.
     *
     * @param sectionType CalendarSectionType
     * @return List<CalendarSupplementalEntry>
     */
    public List<CalendarSupplementalEntry> getEntriesBySection(CalendarSectionType sectionType) {
        return this.sectionEntries.get(sectionType);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarSupplemental)) return false;
        if (!super.equals(o)) return false;
        CalendarSupplemental that = (CalendarSupplemental) o;
        if (calDate != null ? !calDate.equals(that.calDate) : that.calDate != null) return false;
        if (calendarId != null ? !calendarId.equals(that.calendarId) : that.calendarId != null) return false;
        if (sectionEntries != null ? !sectionEntries.equals(that.sectionEntries) : that.sectionEntries != null)
            return false;
        if (releaseDateTime != null ? !releaseDateTime.equals(that.releaseDateTime) : that.releaseDateTime != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (calendarId != null ? calendarId.hashCode() : 0);
        result = 31 * result + (calDate != null ? calDate.hashCode() : 0);
        result = 31 * result + (releaseDateTime != null ? releaseDateTime.hashCode() : 0);
        result = 31 * result + (sectionEntries != null ? sectionEntries.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
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

    public LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getSectionEntries() {
        return sectionEntries;
    }

    public void setSectionEntries(LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries) {
        this.sectionEntries = sectionEntries;
    }
}
