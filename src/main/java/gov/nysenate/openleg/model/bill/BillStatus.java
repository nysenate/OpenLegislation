package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.CommitteeId;

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
    protected int actionSequenceNo;
    protected LocalDate actionDate;
    protected CommitteeId committeeId;
    protected Integer calendarNo;

    /** --- Constructors --- */

    public BillStatus(BillStatusType statusType, LocalDate actionDate) {
        this.statusType = statusType;
        this.actionDate = actionDate;
    }

    /** --- Overrides --- */

    @Override
    public int hashCode() {
        return Objects.hash(statusType, actionSequenceNo, actionDate, committeeId, calendarNo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillStatus other = (BillStatus) obj;
        return Objects.equals(this.statusType, other.statusType) &&
               Objects.equals(this.actionSequenceNo, other.actionSequenceNo) &&
               Objects.equals(this.actionDate, other.actionDate) &&
               Objects.equals(this.committeeId, other.committeeId) &&
               Objects.equals(this.calendarNo, other.calendarNo);
    }

    @Override
    public String toString() {
        return this.statusType + " (" + this.actionDate + ") " + ((committeeId != null) ? committeeId : "") +
                ((calendarNo != null) ? " Cal No: " + calendarNo : "");
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

    public int getActionSequenceNo() {
        return actionSequenceNo;
    }

    public void setActionSequenceNo(int actionSequenceNo) {
        this.actionSequenceNo = actionSequenceNo;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(CommitteeId committeeId) {
        this.committeeId = committeeId;
    }

    public Integer getCalendarNo() {
        return calendarNo;
    }

    public void setCalendarNo(Integer calendarNo) {
        this.calendarNo = calendarNo;
    }
}