package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertDao;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.spotchecks.base.SpotCheckReportRunMode;
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

    @Override
    public SpotCheckReportRunMode getRunMode() {
        return SpotCheckReportRunMode.PERIODIC;
    }
}
