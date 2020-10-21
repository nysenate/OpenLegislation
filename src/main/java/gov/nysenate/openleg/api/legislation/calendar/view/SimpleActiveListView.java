package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.legislation.calendar.CalendarActiveList;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SimpleActiveListView extends CalendarActiveListIdView{

    protected LocalDate calDate;

    protected LocalDateTime releaseDateTime;

    protected String notes;

    protected int totalEntries;

    public SimpleActiveListView(CalendarActiveList activeList) {
        super(activeList.getCalendarActiveListId());
        this.calDate = activeList.getCalDate();
        this.releaseDateTime = activeList.getReleaseDateTime();
        this.notes = activeList.getNotes();
        this.totalEntries = activeList.getEntries().size();
    }

    //Added for Json deserialization
    protected SimpleActiveListView() {}

    public LocalDate getCalDate() {
        return calDate;
    }

    public LocalDateTime getReleaseDateTime() {
        return releaseDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist-simple";
    }
}
