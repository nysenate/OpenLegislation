package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SOBIFragment;

public interface BillDao
{
    /**
     * Retrieve a Bill via a BillId.
     * @param billId BillId
     * @return Bill object if found, null otherwise.
     */
    public Bill getBill(BillId billId);

    public void updateBill(Bill bill, SOBIFragment sobiFragment);

    public void deleteBill(Bill bill);

    public void publishBill(Bill bill);

    public void unPublishBill(Bill bill);

    public void deleteAllBills();
}
