package gov.nysenate.openleg.dao.bill.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
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
     * @param query
     * @param limOff
     * @return
     */
    public SearchResults<BillId> searchAll(String query, LimitOffset limOff);

    /**
     *
     *
     * @param query
     * @param limOff
     * @return
     */
    public SearchResults<BillId> searchAdvanced(Map<BillSearchField, String> query, LimitOffset limOff);
}