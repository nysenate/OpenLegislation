package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.model.calendar.CalendarActiveListId;

public class CalendarActiveListIdView extends CalendarIdView{

    protected int sequenceNumber;

    public CalendarActiveListIdView(CalendarActiveListId calendarActiveListId) {
        super(calendarActiveListId);
        this.sequenceNumber = calendarActiveListId.getSequenceNo();
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist-id";
    }
}
