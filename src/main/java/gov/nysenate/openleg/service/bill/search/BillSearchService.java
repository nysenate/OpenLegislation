package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.event.BillUpdateEvent;
import gov.nysenate.openleg.service.bill.event.BulkBillUpdateEvent;

import java.util.Collection;

public interface BillSearchService
{
    /**
     * Performs a search across all bill data.
     *
     * @see #searchBills(String, SessionYear, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Matches all bills for the given session year.
     *
     * @see #searchBills(String, SessionYear, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<BaseBillId> searchBills(SessionYear session, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs search across bills in a given session year.
     *
     * @param query String - General search term
     * @param session SessionYear - Filter by session year
     * @param sort String - Sort by field(s)
     * @param limOff LimitOffset - Restrict the result set.
     * @return SearchResults<BaseBillId>
     * @throws SearchException
     */
    public SearchResults<BaseBillId> searchBills(String query, SessionYear session, String sort, LimitOffset limOff)
        throws SearchException;

    public void handleBillUpdate(BillUpdateEvent billUpdateEvent);

    public void handeBulkBillUpdate(BulkBillUpdateEvent bulkBillUpdateEvent);

    public void updateBillIndex(Bill bill);

    public void updateBillIndices(Collection<Bill> bills);
}