package gov.nysenate.openleg.dao.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.service.bill.search.BillSearchField;

import java.util.Map;

/**
 * DAO interface for searching Bill data.
 */
public interface BillSearchDao
{
    /**
     *
     *
     * @param query String
     * @param sort String
     * @param limOff LimitOffset
     * @return SearchResults<BillId>
     */
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff);
}