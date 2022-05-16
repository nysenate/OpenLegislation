package gov.nysenate.openleg.updates.bill;

import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public record BulkBillUpdateEvent(Collection<Bill> bills) implements ContentUpdateEvent {}
