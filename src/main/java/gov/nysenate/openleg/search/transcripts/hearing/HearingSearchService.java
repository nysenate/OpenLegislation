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
    default SearchResults<HearingId> searchHearings(Integer year, String sort, LimitOffset limOff)
            throws SearchException {
        return searchHearings(null, year, sort, limOff);
    }

    /**
     * Performs a search across all hearings.
     */
    default SearchResults<HearingId> searchHearings(String queryStr, String sort, LimitOffset limOff)
            throws SearchException {
        return searchHearings(queryStr, null, sort, limOff);
    }

    /**
     * Performs a search across all hearings in a given year.
     * @param queryStr Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s).
     * @param limOff Restrict the result set.
     */
    SearchResults<HearingId> searchHearings(String queryStr, Integer year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Handles a hearing update event by indexing the supplied hearing.
     */
    void handleHearingUpdate(HearingUpdateEvent hearingUpdateEvent);
}
