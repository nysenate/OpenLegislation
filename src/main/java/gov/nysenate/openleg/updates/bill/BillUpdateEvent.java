package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;

public class BillUpdateEvent extends ContentUpdateEvent
{
    protected Bill bill;

    /** --- Constructors --- */

    public BillUpdateEvent(Bill bill, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.bill = bill;
    }

    /** --- Basic Getters/Setters --- */

    public Bill getBill() {
        return bill;
    }
}