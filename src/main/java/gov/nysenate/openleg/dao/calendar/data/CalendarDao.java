package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * DAO interface for retrieving and persisting calendar data.
 */
public interface CalendarDao
{
    /**
     * Get a Calendar instance via the CalendarId.
     *
     * @param calendarId CalendarId
     * @return Calendar
     * @throws org.springframework.dao.DataAccessException
     */
    Calendar getCalendar(CalendarId calendarId) throws DataAccessException;

    /**
     * Gets an active list calendar corresponding to the given active list id
     *
     * @param calendarActiveListId
     * @return CalendarActiveList
     * @throws DataAccessException
     */
    CalendarActiveList getActiveList(CalendarActiveListId calendarActiveListId) throws DataAccessException;

    /**
     * Returns a calendar supplemental corresponding to the given calendar supplemental id.
     *
     * @param calendarSupplementalId
     * @return CalendarSupplemental
     * @throws DataAccessException
     */
    CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId calendarSupplementalId) throws DataAccessException;

    /**
     * Returns a range containing all years for which calendar data is present
     * @return Range<Integer>
     */
    Range<Integer> getActiveYearRange();

    /**
     * Gets the total number of stored calendars
     * @return int
     */
    int getCalendarCount();

    /**
     * Gets the number of calendars that exist for the given year
     * @param year
     * @return int
     */
    int getCalendarCount(int year);

    /**
     * Gets the number of active lists that exist for the given year
     * @param year
     * @return int
     */
    int getActiveListCount(int year);

    /**
     * Gets the number of calendar supplemental that exist for the given year
     * @param year
     * @return int
     */
    int getCalendarSupplementalCount(int year);

    /**
     * Returns a list of all the calendars for the given year, sorted by the calendar no.
     *
     * @param year int - The year to retrieve calendar ids for.
     * @param calOrder SortOrder - Order by the calendar no.
     * @param limitOffset
     * @return List<CalendarId>
     */
    List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset);

    /**
     * Returns a list of all the active lists for the given year, sorted by calendar number and sequence number.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarActiveList>
     * @throws DataAccessException
     */
    List<CalendarActiveListId> getActiveListIds(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Returns a list of all the calendar supplementals for a given year, sorted by calendar number and supplemental id.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarSupplemental>
     * @throws DataAccessException
     */
    List<CalendarSupplementalId> getCalendarSupplementalIds(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Updates the calendar or inserts it if it does not yet exist. Associates the
     * the LegDataFragment that triggered the update (set null if not applicable).
     *
     * @param calendar Calendar - The calendar to save.
     * @param legDataFragment LegDataFragment - The fragment that triggered this update.
     */
    void updateCalendar(Calendar calendar, LegDataFragment legDataFragment) throws DataAccessException;
}