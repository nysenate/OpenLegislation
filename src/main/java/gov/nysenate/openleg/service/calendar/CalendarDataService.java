package gov.nysenate.openleg.service.calendar;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.sobi.SobiFragment;

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
     * @throws CalendarNotFoundEx = If no Calendar was matched via the given id.
     */
    public Calendar getCalendar(CalendarId calendarId) throws CalendarNotFoundEx;

    /**
     * Saves the Calendar into the persistence layer. If a new Calendar reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     *
     * @param calendar Calendar
     * @param sobiFragment SobiFragment
     */
    public void saveCalendar(Calendar calendar, SobiFragment sobiFragment);
}
