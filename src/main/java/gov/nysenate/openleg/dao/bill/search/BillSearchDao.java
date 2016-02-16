package gov.nysenate.openleg.dao.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching Bill data.
 */
public interface BillSearchDao
{
    /**
     * Performs a free-form search across all the bills using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param rescorer RescoreBuilder.Rescorer - Fine tune the ranking
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    public SearchResults<BaseBillId> searchBills(QueryBuilder query, FilterBuilder filter, RescoreBuilder.Rescorer rescorer,
                                                 List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Update the bill index with the content of the supplied bill.
     *
     * @param bill Bill
     */
    public void updateBillIndex(Bill bill);

    /**
     * Updates the bill index with the content of the supplied Bills.
     *
     * @param bills Collection<Bill>
     */
    public void updateBillIndex(Collection<Bill> bills);

    /**
     * Removes the bill from the search index with the given id.
     *
     * @param baseBillId BaseBillId
     */
    public void deleteBillFromIndex(BaseBillId baseBillId);
}