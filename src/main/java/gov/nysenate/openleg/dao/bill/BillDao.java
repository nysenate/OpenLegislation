package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.sobi.SOBIFragment;

public interface BillDao
{
    public Bill getBill(String printNo, int sessionYear);

    public void updateBill(Bill bill, SOBIFragment sobiFragment);

    public void deleteBill(Bill bill);

    public void publishBill(Bill bill);

    public void unPublishBill(Bill bill);

    public void deleteAllBills();
}
