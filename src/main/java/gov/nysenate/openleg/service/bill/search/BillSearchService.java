package gov.nysenate.openleg.service.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchException;
import gov.nysenate.openleg.service.base.SearchResults;

import java.util.Map;

public interface BillSearchService
{
    /**
     * Performs a search across all bill data.
     *
     * @param query String - General search term
     * @param limOff LimitOffset - Restrict the result set.
     * @return SearchResults<BaseBillId>
     */
    public SearchResults<BaseBillId> searchBills(String query, LimitOffset limOff) throws SearchException;


}