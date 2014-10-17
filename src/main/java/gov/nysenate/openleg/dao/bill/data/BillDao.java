package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * DAO interface for retrieving and persisting Bill data.
 */
public interface BillDao
{
    public Bill getBill(BillId billId) throws DataAccessException;

    public BillInfo getBillInfo(BillId billId) throws DataAccessException;

    public void applyText(Bill bill) throws DataAccessException;

    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limOff, SortOrder billIdSort) throws DataAccessException;

    public int getBillCount() throws DataAccessException;

    public int getBillCount(SessionYear sessionYear) throws DataAccessException;

    /**
     * Updates the bill or inserts it if it does not yet exist. Associates
     * the SobiFragment that triggered the update (set null if not applicable).
     *
     * @param bill Bill
     * @param sobiFragment SobiFragment
     * @throws DataAccessException - If there was an error while trying to save the Bill.
     */
    public void updateBill(Bill bill, SobiFragment sobiFragment) throws DataAccessException;

    public void publishBill(Bill bill);

    public void unPublishBill(Bill bill);
}
