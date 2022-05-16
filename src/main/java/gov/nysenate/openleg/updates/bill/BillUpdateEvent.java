package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record BillUpdateEvent(Bill bill) implements ContentUpdateEvent {}
