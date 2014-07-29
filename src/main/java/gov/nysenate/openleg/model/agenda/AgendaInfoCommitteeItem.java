package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;

import java.io.Serializable;

/**
 * An AgendaInfoCommitteeItem indicates a specific bill that will be brought up for consideration.
 */
public class AgendaInfoCommitteeItem implements Serializable
{
    private static final long serialVersionUID = -8904159113309808493L;

    /** Reference to the bill id associated with the bill being considered */
    private BillId billId;

    /** An optional message associated with this item. */
    private String message;

    /** --- Constructors --- */

    public AgendaInfoCommitteeItem() {}

    public AgendaInfoCommitteeItem(BillId billId, String message) {
        this();
        this.setBillId(billId);
        this.setMessage(message);
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaInfoCommitteeItem)) return false;
        AgendaInfoCommitteeItem that = (AgendaInfoCommitteeItem) o;
        if (billId != null ? !billId.equals(that.billId) : that.billId != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = billId != null ? billId.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    /** --- Basic Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}