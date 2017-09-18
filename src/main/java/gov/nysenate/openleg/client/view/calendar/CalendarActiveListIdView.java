package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarType;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;

public class CalendarActiveListIdView extends CalendarIdView{

    protected int sequenceNumber;

    public CalendarActiveListIdView(CalendarActiveListId calendarActiveListId) {
        super(calendarActiveListId);
        this.sequenceNumber = calendarActiveListId.getSequenceNo();
    }

    //Added for Json deserialization
    public CalendarActiveListIdView() {}

    //Added for Json deserialization
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public CalendarEntryListId toCalendarEntryListId() {
        return new CalendarEntryListId(this.toCalendarId(), CalendarType.ACTIVE_LIST, Version.DEFAULT,sequenceNumber);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist-id";
    }
}
