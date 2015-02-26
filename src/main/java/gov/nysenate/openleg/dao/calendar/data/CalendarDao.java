package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

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
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException;

    /**
     * Gets an active list calendar corresponding to the given active list id
     *
     * @param calendarActiveListId
     * @return CalendarActiveList
     * @throws DataAccessException
     */
    public CalendarActiveList getActiveList(CalendarActiveListId calendarActiveListId) throws DataAccessException;

    /**
     * Returns a calendar supplemental corresponding to the given calendar supplemental id.
     *
     * @param calendarSupplementalId
     * @return CalendarSupplemental
     * @throws DataAccessException
     */
    public CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId calendarSupplementalId) throws DataAccessException;

    /**
     * Returns a range containing all years for which calendar data is present
     * @return Range<Integer>
     */
    public Range<Integer> getActiveYearRange();

    /**
     * Gets the total number of stored calendars
     * @return int
     */
    public int getCalendarCount();

    /**
     * Gets the number of calendars that exist for the given year
     * @param year
     * @return int
     */
    public int getCalendarCount(int year);

    /**
     * Gets the number of active lists that exist for the given year
     * @param year
     * @return int
     */
    public int getActiveListCount(int year);

    /**
     * Gets the number of calendar supplemental that exist for the given year
     * @param year
     * @return int
     */
    public int getCalendarSupplementalCount(int year);

    /**
     * Returns a list of all the calendars for the given year, sorted by the calendar no.
     *
     * @param year int - The year to retrieve calendar ids for.
     * @param calOrder SortOrder - Order by the calendar no.
     * @param limitOffset
     * @return List<CalendarId>
     */
    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset);

    /**
     * Returns a list of all the active lists for the given year, sorted by calendar number and sequence number.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarActiveList>
     * @throws DataAccessException
     */
    public List<CalendarActiveListId> getActiveListIds(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Returns a list of all the calendar supplementals for a given year, sorted by calendar number and supplemental id.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return List<CalendarSupplemental>
     * @throws DataAccessException
     */
    public List<CalendarSupplementalId> getCalendarSupplementalIds(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Updates the calendar or inserts it if it does not yet exist. Associates the
     * the SobiFragment that triggered the update (set null if not applicable).
     *
     * @param calendar Calendar - The calendar to save.
     * @param sobiFragment SobiFragment - The fragment that triggered this update.
     */
    public void updateCalendar(Calendar calendar, SobiFragment sobiFragment) throws DataAccessException;
}