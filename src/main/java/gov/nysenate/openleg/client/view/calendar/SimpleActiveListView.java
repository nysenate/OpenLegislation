package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.CalendarActiveList;

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
    public SimpleActiveListView() {}

    //Added for Json deserialization
    public void setCalDate(String calDate) {
        this.calDate = LocalDate.parse(calDate);
    }

    //Added for Json deserialization
    public void setReleaseDateTime(String releaseDateTime) {
        this.releaseDateTime = LocalDateTime.parse(releaseDateTime);
    }

    //Added for Json deserialization
    public void setNotes(String notes) {
        this.notes = notes;
    }

    //Added for Json deserialization
    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

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
