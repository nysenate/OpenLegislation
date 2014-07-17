package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;

public interface BillDataService
{
    public Bill getBill(BillId billId) throws BillNotFoundEx;

    public Bill createBill();

    public void saveBill(Bill bill, SobiFragment fragment);
}
