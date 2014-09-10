package gov.nysenate.openleg.model.bill;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a legislative milestone. Associates the status type with an action date.
 */
public class BillStatus implements Serializable
{
    private static final long serialVersionUID = -7302204265170618518L;

    protected BillStatusType statusType;

    protected LocalDate actionDate;

    /** --- Constructors --- */

    public BillStatus(BillStatusType statusType, LocalDate actionDate) {
        this.statusType = statusType;
        this.actionDate = actionDate;
    }

    /** --- Overrides --- */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillStatus other = (BillStatus) obj;
        return Objects.equals(this.statusType, other.statusType) &&
               Objects.equals(this.actionDate, other.actionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusType, actionDate);
    }

    /** --- Basic Getters/Setters --- */

    public BillStatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(BillStatusType statusType) {
        this.statusType = statusType;
    }

    public LocalDate getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDate actionDate) {
        this.actionDate = actionDate;
    }
}