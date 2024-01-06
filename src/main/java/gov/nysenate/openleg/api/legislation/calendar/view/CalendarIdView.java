package gov.nysenate.openleg.api.legislation.calendar.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.calendar.CalendarId;

public class CalendarIdView implements ViewObject {
    protected int year;
    protected int calendarNumber;

    // Default constructor for deserialization.
    protected CalendarIdView() {}

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

    @JsonIgnore
    public CalendarId toCalendarId() {
        return new CalendarId(calendarNumber,year);
    }

    @Override
    public String getViewType() {
        return "calendar-id";
    }
}