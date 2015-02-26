package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.CalendarSupplemental;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SimpleCalendarSupView extends CalendarSupIdView{

    protected LocalDate calDate;

    protected LocalDateTime releaseDateTime;

    protected int totalEntries;

    public SimpleCalendarSupView(CalendarSupplemental calendarSupplemental) {
        super(calendarSupplemental.getCalendarSupplementalId());
        this.calDate = calendarSupplemental.getCalDate();
        this.releaseDateTime = calendarSupplemental.getReleaseDateTime();
        this.totalEntries = calendarSupplemental.getAllEntries().size();
    }

    public LocalDate getCalDate() {
        return calDate;
    }

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    @Override
    public String getViewType() {
        if (this.version.equals("floor")) {
            return "calendar-floor-simple";
        }
        return "calendar-supplemental-simple";
    }
}
