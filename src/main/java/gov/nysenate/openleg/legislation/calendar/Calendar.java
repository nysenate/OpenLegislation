package gov.nysenate.openleg.legislation.calendar;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

import java.time.LocalDate;
import java.util.*;

/**
 * Each day the Senate is in session an associated calendar is present which lists all the bills
 * that have been reported for consideration, split into sections based on their type and
 * status information.
 */
public class Calendar extends BaseLegislativeContent
{
    /** The calendar id */
    private CalendarId id;

    /** Map of all the supplementals associated with this calendar. */
    private EnumMap<Version, CalendarSupplemental> supplementalMap;

    /** Map of all the active lists associated with this calendar. */
    private TreeMap<Integer, CalendarActiveList> activeListMap;

    /** --- Constructors --- */

    public Calendar() {
        super();
        this.supplementalMap = new EnumMap<>(Version.class);
        this.activeListMap = new TreeMap<>();
    }

    public Calendar(CalendarId calendarId) {
        this();
        this.setId(calendarId);
        this.setYear(calendarId.getYear());
        this.setSession(new SessionYear(getYear()));
    }

    /** --- Functional Getters/Setters --- */

    public CalendarActiveList getActiveList(Integer id) {
        return this.activeListMap.get(id);
    }

    public void putActiveList(CalendarActiveList activeList) {
        this.activeListMap.put(activeList.getSequenceNo(), activeList);
    }

    public void removeActiveList(Integer id) {
        this.activeListMap.remove(id);
    }

    public CalendarSupplemental getSupplemental(Version id) {
        return this.supplementalMap.get(id);
    }

    public void putSupplemental(CalendarSupplemental supplemental) {
        this.supplementalMap.put(supplemental.getVersion(), supplemental);
    }

    public void removeSupplemental(Version id) {
        this.supplementalMap.remove(id);
    }

    public LocalDate getCalDate() {
        if (this.supplementalMap.size() > 0) {
            return this.supplementalMap.values().iterator().next().getCalDate();
        }
        else if (this.activeListMap.size() > 0) {
            return this.activeListMap.values().iterator().next().getCalDate();
        }
        else {
            return null;
        }
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return "Senate Calendar " + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Calendar other = (Calendar) obj;
        return Objects.equals(this.id, other.id) &&
               Objects.equals(this.supplementalMap, other.supplementalMap) &&
               Objects.equals(this.activeListMap, other.activeListMap) &&
               Objects.equals(this.publishedDateTime, other.publishedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, supplementalMap, activeListMap, publishedDateTime);
    }

    /** --- Basic Getters/Setters --- */

    public CalendarId getId() {
        return id;
    }

    public List<CalendarEntryListId> getCalendarEntryListIds() {
        List<CalendarEntryListId> calendarEntryListIds = new ArrayList<>();
        calendarEntryListIds.addAll(this.getSupplementalMap().values().stream()
                .map(CalendarSupplemental::getCalendarSupplementalId)
                .map(CalendarSupplementalId::toCalendarEntryListId)
                .toList());
        calendarEntryListIds.addAll(this.getActiveListMap().values().stream()
                .map(CalendarActiveList::getCalendarActiveListId)
                .map(CalendarActiveListId::toCalendarEntryListId)
                .toList());
        return calendarEntryListIds;
    }

    public void setId(int calenderNumber, int year) {
        this.id = new CalendarId(calenderNumber, year);
    }

    public void setId(CalendarId id) {
        this.id = id;
    }

    public EnumMap<Version, CalendarSupplemental> getSupplementalMap() {
        return supplementalMap;
    }

    public void setSupplementalMap(EnumMap<Version, CalendarSupplemental> supplementalMap) {
        this.supplementalMap = supplementalMap;
    }

    public TreeMap<Integer, CalendarActiveList> getActiveListMap() {
        return activeListMap;
    }

    public void setActiveListMap(TreeMap<Integer, CalendarActiveList> activeListMap) {
        this.activeListMap = activeListMap;
    }
}
