package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseObject;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.Date;

/**
 * Represents a single action on a single bill. E.g. REFERRED TO RULES.*
 * Uniquely identified by Bill+Date.getTime()+Text.
 */
public class BillAction extends BaseObject
{
    /** Print number of the base bill. */
    protected String baseBillPrintNo = "";

    /** The bill amendment version the action was taken on. */
    private String amendmentVersion;

    /** The date this action was performed. Has no time component. */
    private Date date = null;

    /** The text of this action. */
    private String text = "";

    /** --- Constructors --- */

    public BillAction() {
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
        this.date = date;
        this.text = text;
        this.setBaseBillPrintNo(bill.getPrintNo());
        this.setAmendmentVersion(billAmendment);
        this.setPublishDate(this.date);
        this.setModifiedDate(this.date);
        this.setSession(bill.getSession());
    }

    /** --- Functional Getters/Setters --- */

    public int getYear() {
        return new LocalDate(date).getYear();
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillAction that = (BillAction) o;

        if (baseBillPrintNo != null ? !baseBillPrintNo.equals(that.baseBillPrintNo) : that.baseBillPrintNo != null)
            return false;
        if (amendmentVersion != null ? !amendmentVersion.equals(that.amendmentVersion) : that.amendmentVersion != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = baseBillPrintNo != null ? baseBillPrintNo.hashCode() : 0;
        result = 31 * result + (amendmentVersion != null ? amendmentVersion.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return date.toString() + " " + text;
    }

    /** --- Helper classes --- */

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

    /** --- Basic Getters/Setters --- */

    public String getBaseBillPrintNo() {
        return baseBillPrintNo;
    }

    public void setBaseBillPrintNo(String baseBillPrintNo) {
        this.baseBillPrintNo = baseBillPrintNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAmendmentVersion() {
        return amendmentVersion;
    }

    public void setAmendmentVersion(String amendmentVersion) {
        this.amendmentVersion = amendmentVersion;
    }
}
