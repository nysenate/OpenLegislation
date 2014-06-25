package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.BaseLegContent;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.Date;

/**
 * Represents a single action on a single bill. E.g. REFERRED TO RULES.
 */
public class BillAction extends BaseLegContent
{
    /** Identifies the bill this action was taken on. */
    private BillId billId;

    /** The date this action was performed. Has no time component. */
    private Date date = null;

    /** Number used for chronological ordering. */
    private int sequenceNo = 0;

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
     * @param billId - The id of the bill the action was performed on
     */
    public BillAction(Date date, String text, int sequenceNo, BillId billId) {
        super();
        this.date = date;
        this.text = text;
        this.billId = billId;
        this.sequenceNo = sequenceNo;
        this.session = billId.getSession();
        this.setPublishDate(this.date);
        this.setModifiedDate(this.date);
    }

    /** --- Functional Getters/Setters --- */

    public int getYear() {
        return new LocalDate(date).getYear();
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return date.toString() + " " + text;
    }

    /**
     * Every BillAction is assigned a BillId which may contain an amendment version other than
     * the base version. For the sake of equality checking, we will use the base version of the
     * bill id since the actions are stored on the base bill anyways. Seq no, date, and text will
     * also be checked.
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillAction)) return false;
        BillAction that = (BillAction) o;
        if (sequenceNo != that.sequenceNo) return false;
        if (!billId.getBase().equals(that.billId.getBase())) return false;
        if (!date.equals(that.date)) return false;
        if (!text.equals(that.text)) return false;
        return true;
    }

    /**
     * Similar to the equals() method, the hashCode() method will use the base version of the BillId.
     * @return int
     */
    @Override
    public int hashCode() {
        int result = billId.getBase().hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + sequenceNo;
        result = 31 * result + text.hashCode();
        return result;
    }

    /** --- Helper classes --- */

    public static class ByEventSequenceNoAsc implements Comparator<BillAction> {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o1.getSequenceNo(), o2.getSequenceNo());
        }
    }

    public static class ByEventSequenceNoDesc implements Comparator<BillAction> {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o1.getSequenceNo(), o2.getSequenceNo()) * -1;
        }
    }

    /** --- Basic Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
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
}
