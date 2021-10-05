package gov.nysenate.openleg.search.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.transcripts.hearing.BulkPublicHearingUpdateEvent;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateEvent;

public interface PublicHearingSearchService
{
    /**
     * Performs a search of public hearing id's by year. A null year returns all hearings.
     */
    SearchResults<PublicHearingId> searchPublicHearings(Integer year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all public hearings.
     */
    SearchResults<PublicHearingId> searchPublicHearings(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all public hearings in a given year.
     * @param query Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s).
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    SearchResults<PublicHearingId> searchPublicHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Hanldes a public hearing update event by indexing the supplied public hearing.
     * @param publicHearingUpdateEvent
     */
    void handlePublicHearingUpdate(PublicHearingUpdateEvent publicHearingUpdateEvent);

    /**
     * Handles a batch public hearing update by indexing the supplied public hearings.
     * @param bulkPublicHearingUpdateEvent
     */
    void handleBulkPublicHearingUpdate(BulkPublicHearingUpdateEvent bulkPublicHearingUpdateEvent);
}
