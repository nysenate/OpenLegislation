package gov.nysenate.openleg.model.bill;

import java.time.LocalDateTime;

public class BillUpdateToken
{
    private BaseBillId billId;
    private LocalDateTime updatedDateTime;

    /** --- Constructors ---  */

    public BillUpdateToken(BaseBillId billId, LocalDateTime updatedDateTime) {
        this.billId = billId;
        this.updatedDateTime = updatedDateTime;
    }

    /** --- Basic Getters/Setters --- */

    public BaseBillId getBillId() {
        return billId;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
}
