package gov.nysenate.openleg.api.legislation.calendar.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveListId;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarEntryListId;

public class CalendarActiveListIdView extends CalendarIdView {

    protected int sequenceNumber;

    public CalendarActiveListIdView(CalendarActiveListId calendarActiveListId) {
        super(calendarActiveListId);
        this.sequenceNumber = calendarActiveListId.getSequenceNo();
    }

    //Added for Json deserialization
    protected CalendarActiveListIdView() {
    }

    @JsonIgnore
    public CalendarEntryListId toCalendarEntryListId() {
        return this.toCalendarActiveListId().toCalendarEntryListId();
    }

    @JsonIgnore
    public CalendarActiveListId toCalendarActiveListId() {
        return new CalendarActiveListId(this.calendarNumber, this.year, this.sequenceNumber);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist-id";
    }
}
