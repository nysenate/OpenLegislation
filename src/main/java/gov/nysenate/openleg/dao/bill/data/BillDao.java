package gov.nysenate.openleg.dao.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Set;

/**
 * DAO interface for retrieving and persisting Bill data.
 */
public interface BillDao
{
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
     * Retrieves a simple count of all the unique base bills in the database for all session years.
     *
     * @return int
     * @throws DataAccessException - Should only be thrown if there was a fatal error,
     */
    int getBillCount() throws DataAccessException;

    /**
     * Retrieves a simple count of all the unique base bills in the database for a given session year.
     *
     * @param sessionYear SessionYear
     * @return int
     * @throws DataAccessException - Should only be thrown if there was a fatal error
     */
    int getBillCount(SessionYear sessionYear) throws DataAccessException;

    /**
     * Certain bills require alternate urls when linking their pdfs. If the given bill id is one of
     * those bills, return the mapped url.
     * @param billId BillId
     * @return String
     */
    String getAlternateBillPdfUrl(BillId billId) throws DataAccessException;

    /**
     * Returns a range containing the minimum and maximum session years for which there is bill data for.
     * Will throw DataAccessException if there are no bills in the database.
     *
     * @return Range<SessionYear>
     */
    Range<SessionYear> activeSessionRange() throws DataAccessException;

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
