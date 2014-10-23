package gov.nysenate.openleg.dao.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.search.BillSearchField;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for searching Bill data.
 */
public interface BillSearchDao
{
    /**
     * Performs a free-form search across all the bills using the query string syntax.
     *
     * @param query String - Query String
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff);

    public void updateBillIndex(Bill bill);

    /**
     * Updates the bill index with the content of the supplied Bills.
     *
     * @param bills Collection<Bill>
     */
    public void updateBulkBillIndices(Collection<Bill> bills);

    /**
     * Removes the bill from the search index with the given id.
     *
     * @param baseBillId BaseBillId
     */
    public void deleteBillFromIndex(BaseBillId baseBillId);
}