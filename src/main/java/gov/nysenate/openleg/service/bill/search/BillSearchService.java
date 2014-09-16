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
     * @param query
     * @param limOff
     * @return
     */
    public SearchResults<BillId> searchAll(String query, LimitOffset limOff);

    /**
     *
     * @param criteria
     * @param limOff
     * @return
     */
    public SearchResults<BillId> searchAdvanced(Map<BillSearchField, String> criteria, LimitOffset limOff);
}
