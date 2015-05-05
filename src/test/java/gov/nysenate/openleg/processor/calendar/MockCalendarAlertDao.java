package gov.nysenate.openleg.processor.calendar;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.calendar.alert.CalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
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
    public void markAsChecked(CalendarId id) {

    }

    @Override
    public void markProdAsChecked(CalendarId id) {

    }

    @Override
    public List<Calendar> getUnCheckedCalendarAlerts() {
        return null;
    }

    @Override
    public List<Calendar> getProdUnCheckedCalendarAlerts() {
        return null;
    }
}
