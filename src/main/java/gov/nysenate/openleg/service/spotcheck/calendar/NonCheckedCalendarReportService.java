package gov.nysenate.openleg.service.spotcheck.calendar;

import gov.nysenate.openleg.model.calendar.CalendarId;
import org.springframework.stereotype.Service;

@Service
public class NonCheckedCalendarReportService extends CalendarReportService {

    @Override
    protected void markAsChecked(CalendarId id) {
    }
}
