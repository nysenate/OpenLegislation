package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.model.bill.Bill;

import java.util.List;

public interface BillLookupService
{
    public List<Bill> lookupBillsById(String billId);
}
