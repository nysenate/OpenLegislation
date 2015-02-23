package gov.nysenate.openleg.service.law.search;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.law.event.BulkLawUpdateEvent;
import gov.nysenate.openleg.service.law.event.LawUpdateEvent;

import java.time.LocalDateTime;

public interface LawSearchService
{
    /**
     * Search across all law documents.
     * @see #searchLawDocs(String, String, String, LimitOffset)
     */
    public SearchResults<LawDocId> searchLawDocs(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Search across all documents within a specific law volume.
     *
     * @param query String - Lucene query string
     * @param lawId String - The law id to search within. (set to null to search all laws).
     * @param sort String - Optional sort
     * @param limOff LimitOffset - Pagination
     * @return SearchResults<LawDocId>
     * @throws SearchException
     */
    public SearchResults<LawDocId> searchLawDocs(String query, String lawId, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Handle a law update by indexing the supplied law.
     *
     * @param lawUpdateEvent LawUpdateEvent
     */
    public void handleLawUpdate(LawUpdateEvent lawUpdateEvent);

    /**
     * Handle a batch law update by indexing all the supplied laws.
     *
     * @param bulkLawUpdateEvent BulkLawUpdateEvent
     */
    public void handleBulkLawUpdate(BulkLawUpdateEvent bulkLawUpdateEvent);
}
