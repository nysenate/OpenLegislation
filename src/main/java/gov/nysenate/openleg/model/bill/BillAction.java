package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseObject;

import java.util.Comparator;
import java.util.Date;

/**
 * Represents a single action on a single bill. E.g. REFERRED TO RULES
 *
 * Uniquely identified by Bill+Date.getTime()+Text.
 *
 * @author GraylinKim
 */
public class BillAction extends BaseObject
{
    /**
     * This object's unique object id. Bill+Date.getTime()+Text.
     */
    private String oid = "";

    /**
     * The date this action was performed. Has no time component.
     */
    private Date date = null;

    /**
     * The text of this action.
     */
    private String text = "";

    /**
     * The bill this action was taken on.
     */
    private Bill bill = null;



    private String billVersion;

    /**
     * JavaBean constructor.
     */
    public BillAction()
    {
        super();
    }

    /**
     * Fully constructs a new action.
     *
     * @param date - The date of the action
     * @param text - The text of the action
     * @param bill - The bill the action was performed on
     */
    public BillAction(Date date, String text, Bill bill, String billAmendment) {
        super();
        this.bill = bill;
        this.setBillAmendment(billAmendment);
        this.date = date;
        this.text = text;
        this.oid = this.bill.getBillId() + "-" + this.date.getTime() + "-" + this.text;
        this.setPublishDate(this.date);
        this.setModifiedDate(this.date);
        this.setSession(bill.getSession());
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        this.setYear(calendar.get(java.util.Calendar.YEAR));
    }

    /**
     * @return - The object's otype
     */
    public String getOtype()
    {
        return "action";
    }

    /**
     * @return - This object's unique object id.
     */
    public String getOid()
    {
        return this.oid;
    }

    /**
     * @param oid - The object's new oid.
     */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /**
     * @return - The date of the action.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date - The new action date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return - The text of the action.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text - The text of the action.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return - The bill the action was performed on.
     */
    public Bill getBill() {
        return bill;
    }

    /**
     * @param bill - The new bill to this action targets.
     */
    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof BillAction) {
            BillAction other = (BillAction)obj;
            return this.getOid().equals(other.getOid());
        }
        else {
            return false;
        }
    }

    public int getYear() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        return cal.get(java.util.Calendar.YEAR);
    }

    public static class ByEventDate implements Comparator<BillAction> {
        @Override
        public int compare(BillAction be1, BillAction be2) {
            int ret = be1.getDate().compareTo(be2.getDate());
            if(ret == 0) {
                return -1;
            }
            return ret*-1;
        }
    }

    @Override
    public String toString() {
        return date.toString()+" "+text;
    }

    public String getBillAmendment()
    {
        return billVersion;
    }

    public void setBillAmendment(String billVersion)
    {
        this.billVersion = billVersion;
    }
}
