package gov.nysenate.openleg.service.bill.event;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

/** An event that is posted when a specific bill field is updated */
public class BillFieldUpdateEvent extends ContentUpdateEvent {

    /** The bill that was updated */
    private BaseBillId billId;

    /** The field that was updated */
    private BillUpdateField updateField;

    /** --- Constructors --- */

    public BillFieldUpdateEvent(LocalDateTime updateDateTime, BaseBillId billId, BillUpdateField updateField) {
        super(updateDateTime);
        this.billId = billId;
        this.updateField = updateField;
    }

    /** --- Getters --- */

    public BaseBillId getBillId() {
        return billId;
    }

    public BillUpdateField getUpdateField() {
        return updateField;
    }
}
