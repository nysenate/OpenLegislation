package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.calendar.CalendarId;

import java.time.LocalDate;

public class CalendarIdView implements ViewObject {
    protected int year;
    protected int calendarNumber;

    public CalendarIdView(CalendarId calendarId) {
        this.year = calendarId.getYear();
        this.calendarNumber = calendarId.getCalNo();
    }

    public int getYear() {
        return year;
    }

    public int getCalendarNumber() {
        return calendarNumber;
    }

    @Override
    public String getViewType() {
        return "calendar-id";
    }
}
