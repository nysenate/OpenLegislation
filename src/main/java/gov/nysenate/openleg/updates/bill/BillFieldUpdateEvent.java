package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillUpdateField;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record BillFieldUpdateEvent(BaseBillId billId, BillUpdateField updateField)
        implements ContentUpdateEvent {}
