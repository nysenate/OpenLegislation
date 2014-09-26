package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.CalendarSupplemental;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SimpleCalendarSupView extends CalendarSupIdView{

    protected LocalDate calDate;

    protected LocalDateTime releaseDateTime;

    public SimpleCalendarSupView(CalendarSupplemental calendarSupplemental) {
        super(calendarSupplemental.getCalendarSupplementalId());
        this.calDate = calendarSupplemental.getCalDate();
        this.releaseDateTime = calendarSupplemental.getReleaseDateTime();
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }
}
