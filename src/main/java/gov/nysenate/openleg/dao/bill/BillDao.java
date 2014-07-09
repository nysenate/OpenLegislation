package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.springframework.dao.DataAccessException;

public interface BillDao
{
    /**
     * Retrieve a Bill via a BillId.
     * @param billId BillId
     * @return Bill
     * @throws DataAccessException - If there was an error while retrieving the Bill.
     */
    public Bill getBill(BillId billId) throws DataAccessException;

    /**
     * Updates the bill or inserts it if it does not yet exist. Associates the update
     * with the SOBIFragment that triggered the update (set null if not applicable).
     * @param bill Bill
     * @param sobiFragment SOBIFragment
     * @throws DataAccessException - If there was an error while trying to save the Bill.
     */
    public void updateBill(Bill bill, SOBIFragment sobiFragment) throws DataAccessException;

    public void deleteBill(Bill bill);

    public void publishBill(Bill bill);

    public void unPublishBill(Bill bill);

    public void deleteAllBills();
}
