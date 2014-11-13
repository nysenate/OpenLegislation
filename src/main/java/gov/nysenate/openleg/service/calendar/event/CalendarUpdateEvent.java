package gov.nysenate.openleg.service.calendar.event;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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
