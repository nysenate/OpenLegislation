package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.legislation.calendar.CalendarNotFoundEx;
import gov.nysenate.openleg.legislation.calendar.dao.CalendarDataService;
import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarReportService extends BaseCalendarReportService {
    @Autowired
    public CalendarReportService(CalendarCheckService checkService, OpenLegEnvironment environment,
                                 SqlCalendarAlertDao alertDao, CalendarDataService calendarDataService) {
        super(checkService, environment, alertDao, calendarDataService);
    }

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
