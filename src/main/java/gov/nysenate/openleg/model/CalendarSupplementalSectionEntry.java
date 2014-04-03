package gov.nysenate.openleg.model;

public class CalendarSupplementalSectionEntry
{

    /**
     * The unique calendar number for this entry. This is the same for this entry on
     * all calendars during a calendar year.
     */
    private Integer calendarNumber = 0;

    /**
     * The original bill for this calendar entry.
     */
    private Bill bill = null;

    private String billAmendment;

    /**
     * The substituted bill for this calendar entity. null if not substituted.
     */
    private Bill subBill = null;

    private String subBillAmendment;

    /**
     * "HIGH" if bill has not yet properly aged.
     */
    private Boolean billHigh = false;

    /**
     * JavaBean Constructor
     */
    public CalendarSupplementalSectionEntry()
    {

    }

    /**
     * Fully constructs a calendar entry.
     *
     * @param no
     * @param high
     * @param motionDate
     * @param bill
     * @param subBill
     */
    public CalendarSupplementalSectionEntry(Integer no, Bill bill, String billAmendment, Boolean high, Bill subBill, String subBillAmendment)
    {
        this.setCalendarNumber(no);
        this.setBillHigh(high);
        this.setBill(bill);
        this.setBillAmendment(subBillAmendment);
        this.setSubBill(subBill);
        this.setSubBillAmendment(subBillAmendment);
    }

    public Boolean isBillHigh()
    {
        return billHigh;
    }

    public void setBillHigh(Boolean billHigh)
    {
        this.billHigh = billHigh;
    }

    public Bill getBill()
    {
        return bill;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public Bill getSubBill()
    {
        return subBill;
    }

    public void setSubBill(Bill subBill)
    {
        this.subBill = subBill;
    }

    public Integer getCalendarNumber()
    {
        return calendarNumber;
    }

    public void setCalendarNumber(Integer number)
    {
        this.calendarNumber = number;
    }

    public String getBillAmendment()
    {
        return billAmendment;
    }

    public void setBillAmendment(String billAmendment)
    {
        this.billAmendment = billAmendment;
    }

    public String getSubBillAmendment()
    {
        return subBillAmendment;
    }

    public void setSubBillAmendment(String subBillAmendment)
    {
        this.subBillAmendment = subBillAmendment;
    }
}
