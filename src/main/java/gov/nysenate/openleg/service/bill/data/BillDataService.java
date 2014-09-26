package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.List;

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
     * @param billId BillId
     * @return Bill
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    public Bill getBill(BillId billId) throws BillNotFoundEx;

    /**
     * Retrieve a BillInfo instance for the matching BillId. This contains
     * less information that the Bill for purposes of displaying in listings.
     *
     * @param billId BillId
     * @return BillInfo
     * @throws BillNotFoundEx - If no Bill matching the BillId was found.
     */
    public BillInfo getBillInfo(BillId billId) throws BillNotFoundEx;

    /**
     * Retrieve a list of BaseBillIds within the specified session year.
     *
     * @param sessionYear The session year to retrieve bills for
     * @param limitOffset Restrict the result set
     * @return List<BaseBillId>
     */
    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limitOffset);

    /**
     * Saves the Bill in the persistence layer. If a new Bill reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values.
     *
     * @param bill Bill
     * @param fragment SobiFragment
     */
    public void saveBill(Bill bill, SobiFragment fragment);
}
