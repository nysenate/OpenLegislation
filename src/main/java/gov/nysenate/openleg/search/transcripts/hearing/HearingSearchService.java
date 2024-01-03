package gov.nysenate.openleg.search.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateEvent;

public interface HearingSearchService {
    /**
     * Performs a search of hearing id's by year. A null year returns all hearings.
     */
    SearchResults<HearingId> searchHearings(Integer year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all hearings.
     */
    SearchResults<HearingId> searchHearings(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all hearings in a given year.
     * @param query Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s).
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    SearchResults<HearingId> searchHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Hanldes a hearing update event by indexing the supplied hearing.
     * @param hearingUpdateEvent
     */
    void handleHearingUpdate(HearingUpdateEvent hearingUpdateEvent);
}
