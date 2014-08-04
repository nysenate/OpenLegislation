package gov.nysenate.openleg.model.agenda;

import gov.nysenate.openleg.model.bill.BillId;

import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AgendaInfoCommitteeItem other = (AgendaInfoCommitteeItem) obj;
        return Objects.equals(this.billId, other.billId) &&
               Objects.equals(this.message, other.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId, message);
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