package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.dao.calendar.alert.SqlCalendarAlertDao;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IntervalCalendarReportService extends CalendarReportService {

    @Autowired
    private SqlCalendarAlertDao alertDao;

    @Override
    protected List<Calendar> getReferences(LocalDateTime start, LocalDateTime end) {
        return alertDao.getCalendarAlertsByDateRange(start, end);
    }

    @Override
    protected void markAsChecked(CalendarId id) {
    }

    @Override
    protected String getNotes() {
        return "digest";
    }
}
