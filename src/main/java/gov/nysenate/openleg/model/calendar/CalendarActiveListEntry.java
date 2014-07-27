package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

public class CalendarActiveListEntry
{
    /** This calendar number refers to a specific entry on the calendar.
     *  This value is consistent for this entry across all calendars during a year. */
    private Integer billCalNo;

    /** The BillId referenced in this active list entry. */
    private BillId billId;

    /** --- Constructors --- */

    public CalendarActiveListEntry() {}

    public CalendarActiveListEntry(Integer calNo, BillId billId) {
        this();
        this.setBillCalNo(calNo);
        this.setBillId(billId);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarActiveListEntry)) return false;
        CalendarActiveListEntry that = (CalendarActiveListEntry) o;
        if (billCalNo != null ? !billCalNo.equals(that.billCalNo) : that.billCalNo != null) return false;
        if (billId != null ? !billId.equals(that.billId) : that.billId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = billCalNo != null ? billCalNo.hashCode() : 0;
        result = 31 * result + (billId != null ? billId.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public Integer getBillCalNo() {
        return billCalNo;
    }

    public void setBillCalNo(Integer billCalNo) {
        this.billCalNo = billCalNo;
    }

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }
}
