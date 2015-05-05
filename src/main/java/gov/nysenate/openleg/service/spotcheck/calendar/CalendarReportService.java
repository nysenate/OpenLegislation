package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarReportService extends BaseCalendarReportService {

    @Autowired
    private SqlCalendarAlertDao alertDao;

    @Override
    protected String getReportNotes() {
        return "";
    }

    @Override
    protected List<Calendar> getReferences(LocalDateTime start, LocalDateTime end) {
        return alertDao.getUnCheckedCalendarAlerts();
    }

    @Override
    protected void markAsChecked(CalendarId id) {
        alertDao.markAsChecked(id);
    }
}
