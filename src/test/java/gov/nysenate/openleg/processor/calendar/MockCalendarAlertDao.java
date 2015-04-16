package gov.nysenate.openleg.processor.calendar;

import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import org.springframework.dao.DataAccessException;

public class MockCalendarAlertDao extends SqlCalendarAlertDao {

    @Override
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        return new Calendar(calendarId);
    }
}
