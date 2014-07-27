package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface CalendarDao
{
    /**
     * Get a Calendar instance via the CalendarId.
     *
     * @param calendarId CalendarId
     * @return Calendar
     */
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException;

    /**
     * Returns a list of all the calendar ids for the given year, sorted by the calendar no.
     *
     * @param year int - The year to retrieve calendar ids for.
     * @param calOrder SortOrder - Order by the calendar no.
     * @return List<CalendarId>
     */
    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder);

    /**
     * Updates the calendar or inserts it if it does not yet exist. Associates the
     * the SobiFragment that triggered the update (set null if not applicable).
     *
     * @param calendar
     * @param sobiFragment
     */
    public void updateCalendar(Calendar calendar, SobiFragment sobiFragment) throws DataAccessException;
}