package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

public class CalendarSupplementalEntry
{
    /** This calendar number refers to a specific entry on the calendar.
     *  This value is consistent for this entry across all calendars during a year. */
    private Integer billCalNo = 0;

    /** The section this calendar entry belongs in. */
    private CalendarSectionType sectionType;

    /** The BillId referenced in this calendar entry. */
    private BillId billId;

    /** The substituted bill's BillId for this calendar entity. null if not substituted. */
    private BillId subBillId;

    /** "HIGH" if bill has not yet properly aged. */
    private Boolean billHigh = false;

    /** --- Constructors --- */

    public CalendarSupplementalEntry() {}

    public CalendarSupplementalEntry(Integer billCalNo, CalendarSectionType sectionType,
                                     BillId billId, BillId subBillId, Boolean billHigh) {
        this.billCalNo = billCalNo;
        this.sectionType = sectionType;
        this.billId = billId;
        this.subBillId = subBillId;
        this.billHigh = billHigh;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalendarSupplementalEntry)) return false;
        CalendarSupplementalEntry that = (CalendarSupplementalEntry) o;
        if (billHigh != null ? !billHigh.equals(that.billHigh) : that.billHigh != null) return false;
        if (billId != null ? !billId.equals(that.billId) : that.billId != null) return false;
        if (billCalNo != null ? !billCalNo.equals(that.billCalNo) : that.billCalNo != null)
            return false;
        if (sectionType != that.sectionType) return false;
        if (subBillId != null ? !subBillId.equals(that.subBillId) : that.subBillId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = billCalNo != null ? billCalNo.hashCode() : 0;
        result = 31 * result + (sectionType != null ? sectionType.hashCode() : 0);
        result = 31 * result + (billId != null ? billId.hashCode() : 0);
        result = 31 * result + (subBillId != null ? subBillId.hashCode() : 0);
        result = 31 * result + (billHigh != null ? billHigh.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public Integer getBillCalNo() {
        return billCalNo;
    }

    public void setBillCalNo(Integer billCalNo) {
        this.billCalNo = billCalNo;
    }

    public CalendarSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(CalendarSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public BillId getSubBillId() {
        return subBillId;
    }

    public void setSubBillId(BillId subBillId) {
        this.subBillId = subBillId;
    }

    public Boolean getBillHigh() {
        return billHigh;
    }

    public void setBillHigh(Boolean billHigh) {
        this.billHigh = billHigh;
    }
}
