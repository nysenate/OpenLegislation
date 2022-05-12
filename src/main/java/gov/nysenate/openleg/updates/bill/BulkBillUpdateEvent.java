package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkBillUpdateEvent extends ContentUpdateEvent {
    private final Collection<Bill> bills;

    public BulkBillUpdateEvent(Collection<Bill> bills) {
        super();
        this.bills = bills;
    }

    public Collection<Bill> getBills() {
        return bills;
    }
}
