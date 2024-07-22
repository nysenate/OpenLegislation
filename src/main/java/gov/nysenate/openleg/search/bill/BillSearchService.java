package gov.nysenate.openleg.search.bill;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.bill.BillUpdateEvent;
import gov.nysenate.openleg.updates.bill.BulkBillUpdateEvent;

public interface BillSearchService
{
    /**
     * Performs a search across all bill data.
     *
     * @see #searchBills(String, SessionYear, String, LimitOffset)
     */
    default SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff)
            throws SearchException {
        return searchBills(query, null, sort, limOff);
    }

    /**
     * Matches all bills for the given session year.
     *
     * @see #searchBills(String, SessionYear, String, LimitOffset)
     */
    default SearchResults<BaseBillId> searchBills(SessionYear session, String sort, LimitOffset limOff)
            throws SearchException {
        return searchBills(null, session, sort, limOff);
    }

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
    SearchResults<BaseBillId> searchBills(String query, SessionYear session, String sort, LimitOffset limOff)
        throws SearchException;

    /**
     * Handle a bill update event by indexing the supplied bill in the update.
     *
     * @param billUpdateEvent BillUpdateEvent
     */
    void handleBillUpdate(BillUpdateEvent billUpdateEvent);

    /**
     * Handle a batch bill update event by indexing the supplied bills in the update.
     *
     * @param bulkBillUpdateEvent BulkBillUpdateEvent
     */
    void handleBulkBillUpdate(BulkBillUpdateEvent bulkBillUpdateEvent);
}