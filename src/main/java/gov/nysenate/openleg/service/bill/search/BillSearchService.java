package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.data.BillUpdateEvent;
import gov.nysenate.openleg.service.bill.data.BulkBillUpdateEvent;

import java.util.Collection;
import java.util.Map;

public interface BillSearchService
{
    /**
     * Performs a search across all bill data.
     *
     * @param query String - General search term
     * @param sort String - Sort by field(s)
     * @param limOff LimitOffset - Restrict the result set.
     * @return SearchResults<BaseBillId>
     */
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff) throws SearchException;

    public void handleBillUpdate(BillUpdateEvent billUpdateEvent);

    public void handeBulkBillUpdate(BulkBillUpdateEvent bulkBillUpdateEvent);

    public void updateBillIndex(Bill bill);

    public void updateBillIndices(Collection<Bill> bills);
}