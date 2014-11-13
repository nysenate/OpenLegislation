package gov.nysenate.openleg.service.calendar.data;

import gov.nysenate.openleg.model.calendar.CalendarId;

public class CalendarNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -5337097504936947862L;

    private CalendarId calendarId;

    public CalendarNotFoundEx(CalendarId calendarId) {
        this(calendarId, null);
    }
    
    public CalendarNotFoundEx(CalendarId calendarId, Throwable cause) {
        super("Calendar with " + calendarId + " could not be retrieved.", cause);
        this.calendarId = calendarId;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }
}
