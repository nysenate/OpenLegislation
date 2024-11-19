package gov.nysenate.openleg.search.law;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.law.BulkLawUpdateEvent;
import gov.nysenate.openleg.updates.law.LawTreeUpdateEvent;
import gov.nysenate.openleg.updates.law.LawUpdateEvent;

public interface LawSearchService {
    /**
     * Search across all documents within a specific law volume.
     *
     * @param queryStr String - Lucene query string
     * @param lawId String - The law id to search within. (set to null to search all laws).
     * @param sort String - Optional sort
     * @param limOff LimitOffset - Pagination
     * @return SearchResults<LawDocId>
     */
    SearchResults<LawDocId> searchLawDocs(String queryStr, String lawId, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Handle a law update by indexing the supplied law.
     *
     * @param lawUpdateEvent LawUpdateEvent
     */
    void handleLawUpdate(LawUpdateEvent lawUpdateEvent);

    /**
     * Handle a batch law update by indexing all the supplied laws.
     *
     * @param bulkLawUpdateEvent BulkLawUpdateEvent
     */
    void handleBulkLawUpdate(BulkLawUpdateEvent bulkLawUpdateEvent);

    /**
     * Handle a law tree update by clearing and reindexing documents in affected chapters.
     *
     * @param lawTreeUpdateEvent {@link LawTreeUpdateEvent}
     */
    void handleLawTreeUpdate(LawTreeUpdateEvent lawTreeUpdateEvent);
}
