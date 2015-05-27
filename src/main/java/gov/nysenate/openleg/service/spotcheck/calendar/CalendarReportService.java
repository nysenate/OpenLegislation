package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.service.calendar.data.CalendarDataService;
import gov.nysenate.openleg.service.calendar.data.CalendarNotFoundEx;
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
        alertDao.markAsChecked(id);
    }
}
