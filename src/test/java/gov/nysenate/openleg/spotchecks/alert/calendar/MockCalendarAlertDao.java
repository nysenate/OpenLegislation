package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.CalendarAlertDao;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public class MockCalendarAlertDao implements CalendarAlertDao {

    @Override
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        return new Calendar(calendarId);
    }

    @Override
    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset) {
        return null;
    }

    @Override
    public void updateCalendar(Calendar calendar, CalendarAlertFile file) throws DataAccessException {

    }

    @Override
    public List<Calendar> getCalendarAlertsByDateRange(LocalDateTime start, LocalDateTime end) {
        return null;
    }

    @Override
    public void updateChecked(CalendarId id, boolean checked) {}

    @Override
    public void markProdAsChecked(CalendarId id) {}

    @Override
    public List<Calendar> getUnCheckedCalendarAlerts() {
        return null;
    }

    @Override
    public List<Calendar> getProdUnCheckedCalendarAlerts() {
        return null;
    }
}
