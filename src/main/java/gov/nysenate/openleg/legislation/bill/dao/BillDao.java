package gov.nysenate.openleg.legislation.bill.dao;

import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * DAO interface for retrieving and persisting Bill data.
 */
public interface BillDao {
    /**
     * Retrieves a full Bill from the database via the BillId or throws a DataAccessException if no
     * result was found.
     *
     * @param billId BillId - The version in the bill id is not used.
     * @return Bill
     * @throws DataAccessException - If no bill was matched
     */
    Bill getBill(BillId billId) throws DataAccessException;

    /**
     * Retrieves a BillInfo for the given BillId. The query time for a BillInfo will be less than that
     * of a full bill retrieval because it has significantly fewer pieces of data to gather.
     *
     * @param billId BillId - The version in the bill id is not used.
     * @return BillInfo
     * @throws DataAccessException - If no bill was matched.
     */
    BillInfo getBillInfo(BillId billId) throws DataAccessException;

    /**
     * This method applies the memo and full text for all amendments contained in the given Bill object.
     * This can be used by caching implementations where the bill object is kept in memory but the references
     * to the full text and memo are dropped to save memory space.
     *
     * @param strippedBill Bill - The stripped Bill object.
     * @throws DataAccessException
     */
    void applyTextAndMemo(Bill strippedBill) throws DataAccessException;

    /**
     * Gets a List of BaseBillIds for the given session year with options to order and limit the results.
     *
     * @param sessionYear SessionYear
     * @param limOff LimitOffset
     * @param billIdSort SortOrder
     * @return List<BaseBillId>
     * @throws DataAccessException
     */
    List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limOff, SortOrder billIdSort) throws DataAccessException;

    /**
     * Certain bills require alternate urls when linking their pdfs. If the given bill id is one of
     * those bills, return the mapped url.
     * @param billId BillId
     * @return String
     */
    String getAlternateBillPdfUrl(BillId billId) throws DataAccessException;

    /**
     * Updates the bill or inserts it if it does not yet exist. Associates
     * the LegDataFragment that triggered the update (set null if not applicable).
     *
     * @param bill Bill
     * @param legDataFragment LegDataFragment
     * @throws DataAccessException - If there was an error while trying to save the Bill.
     */
    void updateBill(Bill bill, LegDataFragment legDataFragment) throws DataAccessException;

    /**
     * Queries for budget bills that don't have full text or an alternate pdf entry, returning their ids.
     *
     * @param sessionYear {@link SessionYear} - session year to query
     * @return {@link List<BillId>}
     */
    List<BillId> getBudgetBillIdsWithoutText(SessionYear sessionYear);
}
