package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResults;

import java.util.Map;

public interface BillSearchService
{
    /**
     * Performs a search across all bill data.
     *
     * @param query String - General search term
     * @param limOff LimitOffset - Restrict the result set.
     * @return SearchResults<BillId>
     */
    public SearchResults<BillId> searchAll(String query, LimitOffset limOff);

    /**
     * Perform an advanced search across all bill data.
     *
     * @param criteria Map<BillSearchField, String> - Match the strings against their associated attribute.
     * @param limOff LimitOffset - Restrict the result set.
     * @return SearchResults<BillId>
     */
    public SearchResults<BillId> searchAdvanced(Map<BillSearchField, String> criteria, LimitOffset limOff);
}