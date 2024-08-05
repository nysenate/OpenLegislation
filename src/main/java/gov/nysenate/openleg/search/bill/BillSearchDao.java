package gov.nysenate.openleg.search.bill;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching Bill data.
 */
public interface BillSearchDao {
    /**
     * Performs a free-form search across all the bills using the query string syntax and a filter.
     *
     * @param query  String - Query Builder
     * @param sort   String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<BaseBillId> searchBills(Query query,
                                          List<SortOptions> sort, LimitOffset limOff);

    /**
     * Updates the bill index with the content of the supplied Bills.
     *
     * @param bills Collection<Bill>
     */
    void updateBillIndex(Collection<Bill> bills);

    /**
     * Removes the bill from the search index with the given id.
     *
     * @param baseBillId BaseBillId
     */
    void deleteBillFromIndex(BaseBillId baseBillId);
}