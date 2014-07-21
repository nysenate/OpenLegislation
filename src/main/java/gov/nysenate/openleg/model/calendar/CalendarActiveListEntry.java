package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

public class CalendarActiveListEntry
{
    /** This calendar number refers to a specific entry on the calendar, not the
     *  number assigned to the calendar container. This value is consistent for this entry
     *  across all calendars during a year. */
    private Integer calendarNumber;

    /** The BillId referenced in this active list entry. */
    private BillId billId;

    /** --- Constructors --- */

    public CalendarActiveListEntry() {}

    public CalendarActiveListEntry(Integer calNo, BillId billId) {
        this();
        this.setCalendarNumber(calNo);
        this.setBillId(billId);
    }

    /** --- Basic Getters/Setters --- */

    public Integer getCalendarNumber() {
        return calendarNumber;
    }

    public void setCalendarNumber(Integer calendarNumber) {
        this.calendarNumber = calendarNumber;
    }

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }
}
