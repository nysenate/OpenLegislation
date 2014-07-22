package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.BaseLegislativeContent;

import java.util.Date;
import java.util.TreeMap;

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
    private TreeMap<String, CalendarSupplemental> supplementalMap;

    /** Map of all the active lists associated with this calendar. */
    private TreeMap<Integer, CalendarActiveList> activeListMap;

    /** --- Constructors --- */

    public Calendar() {
        super();
        this.supplementalMap = new TreeMap<>();
        this.activeListMap = new TreeMap<>();
    }

    public Calendar(CalendarId calendarId) {
        this();
        this.setId(calendarId);
        this.setYear(resolveSessionYear(calendarId.getYear()));
        this.setSession(resolveSessionYear(getYear()));
    }

    /** --- Functional Getters/Setters --- */

    public CalendarActiveList getActiveList(Integer id) {
        return this.activeListMap.get(id);
    }

    public void putActiveList(CalendarActiveList activeList) {
        this.activeListMap.put(activeList.getId(), activeList);
    }

    public void removeActiveList(Integer id) {
        this.activeListMap.remove(id);
    }

    public CalendarSupplemental getSupplemental(String id) {
        return this.supplementalMap.get(id);
    }

    public void putSupplemental(CalendarSupplemental supplemental) {
        this.supplementalMap.put(supplemental.getVersion(), supplemental);
    }

    public void removeSupplemental(String id) {
        this.supplementalMap.remove(id);
    }

    public Date getCalDate() {
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

    /** --- Basic Getters/Setters --- */

    public CalendarId getId() {
        return id;
    }

    public void setId(CalendarId id) {
        this.id = id;
    }

    public TreeMap<String, CalendarSupplemental> getSupplementalMap() {
        return supplementalMap;
    }

    public void setSupplementalMap(TreeMap<String, CalendarSupplemental> supplementalMap) {
        this.supplementalMap = supplementalMap;
    }

    public TreeMap<Integer, CalendarActiveList> getActiveListMap() {
        return activeListMap;
    }

    public void setActiveListMap(TreeMap<Integer, CalendarActiveList> activeListMap) {
        this.activeListMap = activeListMap;
    }
}