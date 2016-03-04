package gov.nysenate.openleg.service.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for retrieving and saving Bill data. Retrieval is based
 * solely on the BillId value and does not expose any search functionality.
 *
 * @see gov.nysenate.openleg.service.bill.search.BillSearchService For search functions.
 */
public interface BillDataService
{
    /**
     * Retrieve a Bill instance for the matching BillId.
     *
     * @param billId BaseBillId
     * @return Bill
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    public Bill getBill(BaseBillId billId) throws BillNotFoundEx;

    /**
     * Retrieve a BillInfo instance for the matching BillId. This contains
     * less information that the Bill for purposes of displaying in listings.
     *
     * @param billId BaseBillId
     * @return BillInfo
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    public BillInfo getBillInfo(BaseBillId billId) throws BillNotFoundEx;

    /**
     * Retrieves a BillInfo instance for the matching BillId. This contains
     * less information that the Bill for the purposes of displaying in listings.
     * If the requested BillInfo is not found, returns a dummy BillInfo that indicates
     * that data is not yet available for the given bill id.
     *
     * @param billId BaseBillId
     * @return BillInfo
     */
    public BillInfo getBillInfoSafe(BaseBillId billId);

    /**
     * Retrieve a list of BaseBillIds within the specified session year in ascending order.
     * This can be useful for functions that need to iterate over the entire collection of
     * bills such as cache warming and search indexing.
     *
     * @param sessionYear The session year to retrieve bills for
     * @param limitOffset Restrict the result set
     * @return List<BaseBillId>
     */
    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limitOffset);

    /**
     * Get the total number of bills for the given session year. This count includes
     * all bills, including those that have been unpublished.
     *
     * @param sessionYear SessionYear
     * @return int
     */
    public int getBillCount(SessionYear sessionYear);

    /**
     * Saves the Bill in the persistence layer. If a new Bill reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     *
     * @param bill Bill
     * @param fragment SobiFragment
     * @param postUpdateEvent boolean - Set to true if this method should post a BillUpdateEvent
     *                                  to the event bus indicating to subscribers that the bill may have changed.
     */
    public void saveBill(Bill bill, SobiFragment fragment, boolean postUpdateEvent);

    /**
     * Returns a closed Range containing the session years for which bill data exists.
     * If there are no bills in the database, an empty Optional will be returned instead.
     *
     * @return Optional<Range<SessionYear>>
     */
    public Optional<Range<SessionYear>> activeSessionRange();

    /**
     * Certain bills require alternate urls when linking their pdfs. If the given bill id is one of
     * those bills, return the mapped url.
     * @param billId BillId
     * @return Optional<String>
     */
    public Optional<String> getAlternateBillPdfUrl(BillId billId);
}
