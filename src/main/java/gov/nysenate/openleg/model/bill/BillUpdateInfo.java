package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.updates.UpdateToken;

import java.time.LocalDateTime;

public class BillUpdateInfo extends UpdateToken<BaseBillId>
{
    /** Indicates if the bill's status has changed. */
    protected boolean statusChange = false;

    /* --- Constructors --- */

    public BillUpdateInfo(BaseBillId billId, LocalDateTime updatedDateTime) {
        super(billId, updatedDateTime);
    }

    /** --- Basic Getters/Setters --- */

    public boolean isStatusChange() {
        return statusChange;
    }

    public void setStatusChange(boolean statusChange) {
        this.statusChange = statusChange;
    }
}
