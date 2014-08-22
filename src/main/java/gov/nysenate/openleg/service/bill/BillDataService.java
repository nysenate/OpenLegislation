package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillNotFoundEx;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.List;

/**
 * Service interface for retrieving and saving Bill data. Retrieval is based
 * solely on the BillId value and does not expose any search functionality.
 *
 * @see gov.nysenate.openleg.service.bill.BillSearchService For search functions.
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
     *
     *
     * @param sessionYear The session year to retrieve bills for
     * @param limitOffset Restrict the result set
     * @return List<Bill>
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
