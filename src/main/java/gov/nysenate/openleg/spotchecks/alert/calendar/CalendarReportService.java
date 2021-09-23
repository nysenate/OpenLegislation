package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertDao;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.legislation.calendar.CalendarNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarReportService extends BaseCalendarReportService {

    @Autowired
    private SqlCalendarAlertDao alertDao;

    @Autowired
    private CalendarDataService calendarDataService;

    @Override
    protected String getNotes() {
        return "";
    }

    @Override
    protected List<Calendar> getReferences(LocalDateTime start, LocalDateTime end) {
        return alertDao.getUnCheckedCalendarAlerts();
    }

    @Override
    protected Calendar getActualCalendar(CalendarId id, LocalDate calDate) {
        try {
            return calendarDataService.getCalendar(id);
        } catch (CalendarNotFoundEx ex) {
            return null;
        }
    }

    @Override
    protected void markAsChecked(CalendarId id) {
        alertDao.updateChecked(id, true);
    }
}
