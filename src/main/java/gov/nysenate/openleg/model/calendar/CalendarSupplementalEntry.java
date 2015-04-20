package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

import java.util.Objects;

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

    public  CalendarSupplementalEntry(Integer billCalNo, CalendarSectionType sectionType,
                                     BillId billId, BillId subBillId, Boolean billHigh) {
        this.billCalNo = billCalNo;
        this.sectionType = sectionType;
        this.billId = billId;
        this.subBillId = subBillId;
        this.billHigh = billHigh;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CalendarSupplementalEntry other = (CalendarSupplementalEntry) obj;
        return Objects.equals(this.billCalNo, other.billCalNo) &&
               Objects.equals(this.sectionType, other.sectionType) &&
               Objects.equals(this.billId, other.billId) &&
               Objects.equals(this.subBillId, other.subBillId) &&
               Objects.equals(this.billHigh, other.billHigh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billCalNo, sectionType, billId, subBillId, billHigh);
    }

    @Override
    public String toString() {
        return billCalNo.toString() + " " + sectionType.toString() + " " + billId.toString() + " " + subBillId.toString() + " " + billHigh.toString();
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
