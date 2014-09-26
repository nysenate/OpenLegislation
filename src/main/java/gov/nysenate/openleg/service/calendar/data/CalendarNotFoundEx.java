package gov.nysenate.openleg.service.calendar.data;

import gov.nysenate.openleg.model.calendar.CalendarId;

public class CalendarNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -5337097504936947862L;

    private CalendarId calendarId;
    private int year;

    public CalendarNotFoundEx(CalendarId calendarId) {
        super("Calendar with " + calendarId + " could not be retrieved.");
        this.calendarId = calendarId;
    }

    public CalendarNotFoundEx(int year) {
        super("Could not retrieve calendars for " + year);
        this.year = year;
    }

    public CalendarId getCalendarId() {
        return calendarId;
    }

    public int getYear() {
        return year;
    }
}
