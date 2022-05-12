package gov.nysenate.openleg.updates.calendar;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkCalendarUpdateEvent extends ContentUpdateEvent {
    private final Collection<Calendar> calendars;

    public BulkCalendarUpdateEvent(Collection<Calendar> calendars) {
        super();
        this.calendars = calendars;
    }

    public Collection<Calendar> getCalendars() {
        return calendars;
    }
}
