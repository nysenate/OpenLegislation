package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.model.calendar.CalendarId;
import org.springframework.stereotype.Service;

@Service
public class IntervalCalendarReportService extends CalendarReportService {

    @Override
    protected void markAsChecked(CalendarId id) {
    }

    @Override
    protected String getNotes() {
        return "digest";
    }
}
