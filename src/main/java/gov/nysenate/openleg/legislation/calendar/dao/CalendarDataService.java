package gov.nysenate.openleg.legislation.calendar.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.util.List;

/**
 * Service interface for retrieving and saving Calendar data.
 */
public interface CalendarDataService {
    /**
     * Retrieve calendar instances based on the calendar no and year.
     * @param calendarId CalendarId
     * @return Calendar
     * @throws CalendarNotFoundEx - If no Calendar was matched via the given id.
     */
    Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx;

    /**
     * Retrieves an active list corresponding to the given active list id.
     * @return CalendarActiveList
     * @throws CalendarNotFoundEx - If no active list exists that matches the active list id.
     */
    CalendarActiveList getActiveList(CalendarActiveListId activeListId) throws CalendarNotFoundEx;

    /**
     * Retrieves a calendar supplemental corresponding to the given calendar supplemental id.
     * @return CalendarSupplemental
     * @throws CalendarNotFoundEx - if no calendar supplemental exists for the given id.
     */
    CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId supplementalId) throws CalendarNotFoundEx;

    /**
     * Gets a range of all years for which calendar data is present.
     * @return Range<Integer>
     */
    Range<Integer> getCalendarYearRange();

    /**
     * Gets the number of calendars that exist for the given year.
     */
    int getCalendarCount(int year);

    /**
     * Gets the number of active lists that exist for the given year.
     */
    int getActiveListCount(int year);

    /**
     * Gets the number of calendar supplementals that exist for the given year.
     */
    int getSupplementalCount(int year);

    /**
     * Gets all calendars for the given year.
     * @return List<CalendarId>
     * @throws CalendarNotFoundEx - If no calendars exist for the given year.
     */
    List<Calendar> getCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Gets all active lists for the given year.
     * @param limitOffset @return List<CalendarActiveListId>
     * @throws CalendarNotFoundEx - if no active lists exist for the given year.
     */
    List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Gets all calendar supplementals for the given year.
     * @return List<CalendarSupplementalId>
     * @throws CalendarNotFoundEx - if no calendar supplemental exist for the given year.
     */
    List<CalendarSupplemental> getCalendarSupplementals(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Saves the Calendar into the persistence layer. If a new Calendar reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     * @param calendar Calendar
     * @param legDataFragment LegDataFragment
     */
    void saveCalendar(Calendar calendar, LegDataFragment legDataFragment, boolean postUpdateEvent);
}
