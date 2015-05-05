package gov.nysenate.openleg.dao.calendar.alert;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
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

    void markAsChecked(CalendarId id);

    void markProdAsChecked(CalendarId id);

    List<Calendar> getUnCheckedCalendarAlerts();

    List<Calendar> getProdUnCheckedCalendarAlerts();
}
