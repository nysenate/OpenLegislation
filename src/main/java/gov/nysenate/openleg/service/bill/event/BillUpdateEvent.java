package gov.nysenate.openleg.service.bill.event;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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