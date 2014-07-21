package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.bill.BillId;

public class CalendarSupplementalEntry
{
    /** This calendar number refers to a specific entry on the calendar, not the
     *  number assigned to the calendar container. This value is consistent for this entry
     *  across all calendars during a year. */
    private Integer calendarNumber = 0;

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

    public CalendarSupplementalEntry(Integer calendarNumber, CalendarSectionType sectionType,
                                     BillId billId, BillId subBillId, Boolean billHigh) {
        this.calendarNumber = calendarNumber;
        this.sectionType = sectionType;
        this.billId = billId;
        this.subBillId = subBillId;
        this.billHigh = billHigh;
    }

    /** --- Basic Getters/Setters --- */

    public Integer getCalendarNumber() {
        return calendarNumber;
    }

    public void setCalendarNumber(Integer calendarNumber) {
        this.calendarNumber = calendarNumber;
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
