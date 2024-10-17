package gov.nysenate.openleg.legislation.bill.dao.service;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.search.bill.BillSearchService;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for retrieving and saving Bill data. Retrieval is based
 * solely on the BillId value and does not expose any search functionality.
 *
 * @see BillSearchService For search functions.
 */
public interface BillDataService
{

    /**
     * Retrieve a Bill instance for the matching BillId.
     *
     * Will only include bill texts for the given formats.
     *
     * @param billId BaseBillId
     * @return Bill
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    Bill getBill(BaseBillId billId) throws BillNotFoundEx;

    /**
     * Retrieve a BillInfo instance for the matching BillId. This contains
     * less information that the Bill for purposes of displaying in listings.
     *
     * @param billId BaseBillId
     * @return BillInfo
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    BillInfo getBillInfo(BaseBillId billId) throws BillNotFoundEx;

    /**
     * Retrieves a BillInfo instance for the matching BillId. This contains
     * less information that the Bill for the purposes of displaying in listings.
     * If the requested BillInfo is not found, returns a dummy BillInfo that indicates
     * that data is not yet available for the given bill id.
     *
     * @param billId BaseBillId
     * @return BillInfo
     */
    BillInfo getBillInfoSafe(BaseBillId billId);

    /**
     * Retrieve a list of BaseBillIds within the specified session year in ascending order.
     * This can be useful for functions that need to iterate over the entire collection of
     * bills such as cache warming and search indexing.
     *
     * @param sessionYear The session year to retrieve bills for
     * @param limitOffset Restrict the result set
     * @return List<BaseBillId>
     */
    List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limitOffset);

    /**
     * Saves the Bill in the persistence layer. If a new Bill reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     *
     * @param bill Bill
     * @param fragment LegDataFragment
     * @param postUpdateEvent boolean - Set to true if this method should post a BillUpdateEvent
     *                                  to the event bus indicating to subscribers that the bill may have changed.
     */
    void saveBill(Bill bill, LegDataFragment fragment, boolean postUpdateEvent);

    /**
     * Certain bills require alternate urls when linking their pdfs. If the given bill id is one of
     * those bills, return the mapped url.
     * @param billId BillId
     * @return Optional<String>
     */
    Optional<String> getAlternateBillPdfUrl(BillId billId);
}
