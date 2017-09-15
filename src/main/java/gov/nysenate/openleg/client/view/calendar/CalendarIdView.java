package gov.nysenate.openleg.client.view.calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarId;

import java.time.LocalDate;

public class CalendarIdView implements ViewObject {
    protected int year;
    protected int calendarNumber;

    //Default constructor for deserialization.
    protected CalendarIdView(){}

    public CalendarIdView(CalendarId calendarId) {
        this.year = calendarId.getYear();
        this.calendarNumber = calendarId.getCalNo();
    }

    //Added for Json deserialization
    public void setYear(int year) {
        this.year = year;
    }

    //Added for Json deserialization
    public void setCalendarNumber(int calendarNumber) {
        this.calendarNumber = calendarNumber;
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