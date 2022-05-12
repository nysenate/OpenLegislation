package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillUpdateField;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

/** An event that is posted when a specific bill field is updated */
public class BillFieldUpdateEvent extends ContentUpdateEvent {

    /** The bill that was updated */
    private final BaseBillId billId;

    /** The field that was updated */
    private final BillUpdateField updateField;

    public BillFieldUpdateEvent(BaseBillId billId, BillUpdateField updateField) {
        super();
        this.billId = billId;
        this.updateField = updateField;
    }

    public BaseBillId getBillId() {
        return billId;
    }

    public BillUpdateField getUpdateField() {
        return updateField;
    }
}
