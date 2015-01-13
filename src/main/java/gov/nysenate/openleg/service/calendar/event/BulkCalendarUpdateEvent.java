package gov.nysenate.openleg.service.calendar.event;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkCalendarUpdateEvent extends ContentUpdateEvent
{
    protected Collection<Calendar> calendars;

    /** --- Constructors --- */

    public BulkCalendarUpdateEvent(Collection<Calendar> calendars, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.calendars = calendars;
    }

    /** --- Basic Getters --- */

    public Collection<Calendar> getCalendars() {
        return calendars;
    }
}
