package gov.nysenate.openleg.updates.calendar;

import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;

public class CalendarUpdateEvent extends ContentUpdateEvent {

    private Calendar calendar;

    public CalendarUpdateEvent(Calendar calendar, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.calendar = calendar;
    }

    public CalendarUpdateEvent(Calendar calendar) {
        this(calendar, LocalDateTime.now());
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
