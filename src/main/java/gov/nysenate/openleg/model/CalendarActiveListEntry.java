package gov.nysenate.openleg.model;

public class CalendarActiveListEntry
{
    private Integer calendarNumber;
    private Bill bill;
    private String billAmendment;

    public CalendarActiveListEntry()
    {

    }

    public CalendarActiveListEntry(Integer calno, Bill bill, String billAmendment)
    {
        this();
        this.setCalendarNumber(calno);
        this.setBill(bill);
        this.setBillAmendment(billAmendment);
    }

    public Integer getCalendarNumber()
    {
        return calendarNumber;
    }

    public void setCalendarNumber(Integer calendarNumber)
    {
        this.calendarNumber = calendarNumber;
    }

    public Bill getBill()
    {
        return bill;
    }

    public void setBill(Bill bill)
    {
        this.bill = bill;
    }

    public String getBillAmendment()
    {
        return billAmendment;
    }

    public void setBillAmendment(String billAmendment)
    {
        this.billAmendment = billAmendment;
    }
}
