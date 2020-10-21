package gov.nysenate.openleg.spotchecks.alert.calendar.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarAlertFile;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarAlertDao {

    Calendar getCalendar(CalendarId calendarId) throws DataAccessException;

    List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset);

    /**
     * Updates a alert calendar reference, associating the alert file with it.
     * @param calendar
     * @param file
     * @throws DataAccessException
     */
    void updateCalendar(Calendar calendar, CalendarAlertFile file) throws DataAccessException;

    List<Calendar> getCalendarAlertsByDateRange(LocalDateTime start, LocalDateTime end);

    void updateChecked(CalendarId id, boolean checked);

    void markProdAsChecked(CalendarId id);

    List<Calendar> getUnCheckedCalendarAlerts();

    List<Calendar> getProdUnCheckedCalendarAlerts();
}
