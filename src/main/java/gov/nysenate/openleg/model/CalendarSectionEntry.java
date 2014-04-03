package gov.nysenate.openleg.model;

public class CalendarSectionEntry
{

    /**
     * The unique calendar number for this entry. This is the same for this entry on
     * all calendars during a calendar year.
     */
    private Integer number = 0;

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
    public CalendarSectionEntry()
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
    public CalendarSectionEntry(Integer no, Bill bill, String billAmendment, Boolean high, Bill subBill, String subBillAmendment)
    {
        this.setNumber(no);
        this.setBillHigh(high);
        this.setBill(bill);
        this.setBillAmendment(subBillAmendment);
        this.setSubBill(subBill);
        this.setSubBillAmendment(subBillAmendment);
    }

    public Boolean getBillHigh()
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

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(Integer number)
    {
        this.number = number;
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
