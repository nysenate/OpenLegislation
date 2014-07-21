package gov.nysenate.openleg.model.calendar;

import com.google.common.collect.LinkedListMultimap;
import gov.nysenate.openleg.model.BaseLegislativeContent;

import java.util.Date;
import java.util.List;

public class CalendarSupplemental extends BaseLegislativeContent
{
    /** The identifier for this calendar supplemental. Typically a single character. */
    private String id;

    /** Reference to the parent Calendar's id. */
    private CalendarId calendarId;

    /** The calendar date associated with this supplemental. */
    private Date calDate;

    /** The date when this supplemental was released. */
    private Date releaseDateTime;

    /** Mapping of supplemental entries to sections. */
    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> entriesBySection;

    /** --- Constructors --- */

    public CalendarSupplemental() {
        super();
        this.entriesBySection = LinkedListMultimap.create();
    }

    public CalendarSupplemental(CalendarId calId, String id, Date calDate, Date releaseDateTime) {
        this();
        this.setCalendarId(calId);
        this.setYear(calId.getYear());
        this.setSession(resolveSessionYear(getYear()));
        this.setId(id);
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
                entriesBySection.put(entry.getSectionType(), entry);
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
        return this.entriesBySection.get(sectionType);
    }

    /** --- Basic Getters/Setters --- */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getEntriesBySection() {
        return entriesBySection;
    }

    public void setEntriesBySection(LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> entriesBySection) {
        this.entriesBySection = entriesBySection;
    }
}
