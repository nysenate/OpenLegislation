package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a single action on a single bill. E.g. REFERRED TO RULES.
 */
public class BillAction extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = -508975280380827827L;

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
     * also be checked. NOTE: published/modified date are ignored since an individual action
     * is never updated.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillAction other = (BillAction) obj;
        return this.billId.equalsBase(other.billId) &&
               Objects.equals(this.date, other.date) &&
               Objects.equals(this.sequenceNo, other.sequenceNo) &&
               Objects.equals(this.text, other.text);
    }

    @Override
    public int hashCode() {
        return 31 * billId.hashCodeBase() + Objects.hash(date, sequenceNo, text);
    }

    /** --- Helper classes --- */

    public static class ByEventSequenceAsc implements Comparator<BillAction> {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o1.getSequenceNo(), o2.getSequenceNo());
        }
    }

    public static class ByEventSequenceDesc implements Comparator<BillAction> {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o2.getSequenceNo(), o1.getSequenceNo());
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
