package gov.nysenate.openleg.model;

public class AgendaInfoCommitteeItem
{
    private Bill bill;
    private String billAmendment;
    private String message;
    private String title;

    public AgendaInfoCommitteeItem()
    {

    }

    public AgendaInfoCommitteeItem(Bill bill, String billAmendment, String message, String title)
    {
        super();
        this.setBill(bill);
        this.setBillAmendment(billAmendment);
        this.setMessage(message);
        this.setTitle(title);
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

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
