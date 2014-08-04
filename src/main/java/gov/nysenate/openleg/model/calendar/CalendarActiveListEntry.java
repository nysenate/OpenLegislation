package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CalendarActiveListEntry other = (CalendarActiveListEntry) obj;
        return Objects.equals(this.billCalNo, other.billCalNo) &&
               Objects.equals(this.billId, other.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billCalNo, billId);
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
