package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SOBIFragment;

public interface BillDataService
{
    public Bill getBill(BillId billId) throws BillNotFoundEx;

    public void updateBill(Bill bill, SOBIFragment fragment);
}
