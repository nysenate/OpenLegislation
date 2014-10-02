package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
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
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException;

    /**
     * Gets an active list calendar corresponding to the given active list id
     *
     * @param calendarActiveListId
     * @return
     * @throws DataAccessException
     */
    public CalendarActiveList getActiveList(CalendarActiveListId calendarActiveListId) throws DataAccessException;

    /**
     * Returns a floor calendar corresponding to the given calendar supplemental id.
     *
     * @param calendarSupplementalId
     * @return
     * @throws DataAccessException
     */
    public CalendarSupplemental getFloorCalendar(CalendarSupplementalId calendarSupplementalId) throws DataAccessException;

    /**
     * Gets the number of calendars that exist for the given year
     * @param year
     * @return
     */
    public int getCalendarCount(int year);

    /**
     * Gets the number of active lists that exist for the given year
     * @param year
     * @return
     */
    public int getActiveListCount(int year);

    /**
     * Gets the number of floor calendars that exist for the given year
     * @param year
     * @return
     */
    public int getFloorCalendarCount(int year);

    /**
     * Returns a list of all the calendars for the given year, sorted by the calendar no.
     *
     * @param year int - The year to retrieve calendar ids for.
     * @param calOrder SortOrder - Order by the calendar no.
     * @param limitOffset
     * @return List<CalendarId>
     */
    public List<Calendar> getCalendars(int year, SortOrder calOrder, LimitOffset limitOffset);

    /**
     * Returns a list of all the active lists for the given year, sorted by calendar number and sequence number.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return
     * @throws DataAccessException
     */
    public List<CalendarActiveList> getActiveLists(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Returns a list of all the floor calendars for a given year, sorted by calendar number and supplemental id.
     *
     * @param year
     * @param sortOrder
     * @param limitOffset
     * @return
     * @throws DataAccessException
     */
    public List<CalendarSupplemental> getFloorCalendars(int year, SortOrder sortOrder, LimitOffset limitOffset) throws DataAccessException;

    /**
     * Updates the calendar or inserts it if it does not yet exist. Associates the
     * the SobiFragment that triggered the update (set null if not applicable).
     *
     * @param calendar Calendar - The calendar to save.
     * @param sobiFragment SobiFragment - The fragment that triggered this update.
     */
    public void updateCalendar(Calendar calendar, SobiFragment sobiFragment) throws DataAccessException;
}