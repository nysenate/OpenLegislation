package gov.nysenate.openleg.service.calendar.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for retrieving and saving Calendar data.
 */
public interface CalendarDataService
{
    /**
     * Retrieve calendar instances based on the calendar no and year.
     *
     * @param calendarId CalendarId
     * @return Calendar
     * @throws CalendarNotFoundEx - If no Calendar was matched via the given id.
     */
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx;

    /**
     * Retrieves an active list corresponding to the given active list id
     *
     * @param activeListId
     * @return CalendarActiveList
     * @throws CalendarNotFoundEx - If no active list exists that matches the active list id
     */
    public CalendarActiveList getActiveList(CalendarActiveListId activeListId) throws CalendarNotFoundEx;

    /**
     * Retrieves a calendar supplemental corresponding to the given calendar supplemental id
     *
     * @param supplementalId
     * @return CalendarSupplemental
     * @throws CalendarNotFoundEx - if no calendar supplemental exists for the given id
     */
    public CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId supplementalId) throws CalendarNotFoundEx;

    /**
     * Gets a range of all years for which calendar data is present
     * @return Optional<Range<Integer>>
     */
    public Optional<Range<Integer>> getCalendarYearRange();

    /**
     * Gets the total number of stored calendars
     * @return int
     */
    public int getCalendarCount();

    /**
     * Gets the number of calendars that exist for the given year
     *
     * @param year
     * @return
     */
    public int getCalendarCount(int year);

    /**
     * Gets the number of active lists that exist for the given year
     *
     * @param year
     */
    public int getActiveListCount(int year);

    /**
     * Gets the number of calendar supplementals that exist for the given year
     *
     * @param year
     * @return
     */
    public int getSupplementalCount(int year);

    /**
     * Gets all calendars for the given year
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarId>
     * @throws CalendarNotFoundEx - If no calendars exist for the given year
     */
    public List<Calendar> getCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Gets all active lists for the given year
     *
     * @param year
     * @param sortOrder
     * @param limitOffset @return List<CalendarActiveListId>
     * @throws CalendarNotFoundEx - if no active lists exist for the given year
     */
    public List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Gets all calendar supplementals for the given year
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarSupplementalId>
     * @throws CalendarNotFoundEx - if no calendar supplemental exist for the given year
     */
    public List<CalendarSupplemental> getCalendarSupplementals(int year, SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Saves the Calendar into the persistence layer. If a new Calendar reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     *
     * @param calendar Calendar
     * @param sobiFragment SobiFragment
     * @param postUpdateEvent
     */
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment, boolean postUpdateEvent);
}
