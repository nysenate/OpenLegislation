package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CalendarSupplemental extends BaseLegislativeContent
{
    /** The identifier for this calendar supplemental. Typically a single character. */
    private Version version;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** The calendar date associated with this supplemental. */
    private LocalDate calDate;

    /** The date when this supplemental was released. */
    private LocalDateTime releaseDateTime;

    /** Mapping of supplemental entries to sections. */
    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries;

    /** --- Constructors --- */

    public CalendarSupplemental(CalendarId calId, Version version, LocalDate calDate, LocalDateTime releaseDateTime) {
        this.sectionEntries = LinkedListMultimap.create();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(new SessionYear(getYear()));
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CalendarSupplemental other = (CalendarSupplemental) obj;
        return Objects.equals(this.version, other.version) &&
               Objects.equals(this.calendarId, other.calendarId) &&
               Objects.equals(this.calDate, other.calDate) &&
               Objects.equals(this.releaseDateTime, other.releaseDateTime) &&
               Objects.equals(this.sectionEntries, other.sectionEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, calendarId, calDate, releaseDateTime, sectionEntries);
    }

    /** --- Functional Getters/Setters --- */

    public CalendarSupplementalId getCalendarSupplementalId() {
        return new CalendarSupplementalId(calendarId, version);
    }

    public List<CalendarSupplementalEntry> getAllEntries() {
        return sectionEntries.values();
    }

    /** --- Basic Getters/Setters --- */

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

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }

    public void setReleaseDateTime(LocalDateTime releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    public LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getSectionEntries() {
        return sectionEntries;
    }

    public void setSectionEntries(LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries) {
        this.sectionEntries = sectionEntries;
    }
}
