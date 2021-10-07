package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkBillUpdateEvent extends ContentUpdateEvent
{
    protected Collection<Bill> bills;

    public BulkBillUpdateEvent(Collection<Bill> bills, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.bills = bills;
    }

    public Collection<Bill> getBills() {
        return bills;
    }
}