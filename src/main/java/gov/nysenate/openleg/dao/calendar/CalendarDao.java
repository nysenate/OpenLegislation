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
     * Returns a list of all the calendars for the given year, * sorted by the calendar
     * date via the given 'dateOrder'.
     *
     * @param year int
     * @param dateOrder SortOrder
     * @return List<Calendar>
     */
    public List<Calendar> getCalendars(int year, SortOrder dateOrder) throws DataAccessException;

    /**
     * Updates the calendar or inserts it if it does not yet exist. Associates the
     * the SobiFragment that triggered the update (set null if not applicable).
     *
     * @param calendar
     * @param sobiFragment
     */
    public void updateCalendar(Calendar calendar, SobiFragment sobiFragment) throws DataAccessException;
}