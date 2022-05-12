package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class BillUpdateEvent extends ContentUpdateEvent {
    private final Bill bill;

    public BillUpdateEvent(Bill bill) {
        super();
        this.bill = bill;
    }

    public Bill getBill() {
        return bill;
    }
}
