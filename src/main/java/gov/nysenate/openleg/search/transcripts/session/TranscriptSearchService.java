package gov.nysenate.openleg.search.transcripts.session;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;

public interface TranscriptSearchService {
    /**
     * Provides a listing of all transcripts.
     */
    default SearchResults<TranscriptId> searchTranscripts(String sort, LimitOffset limOff,
                                                          boolean sessionOnly) throws SearchException {
        return searchTranscripts(null, sort, limOff, sessionOnly);
    }

    /**
     * Provides a listing of transcripts which took place in a given year.
     */
    default SearchResults<TranscriptId> searchTranscripts(int year, String sort, LimitOffset limOff,
                                                          boolean sessionOnly) throws SearchException {
        return searchTranscripts(null, year, sort, limOff, sessionOnly);
    }

    /**
     * Performs a search across all transcripts.
     */
    default SearchResults<TranscriptId> searchTranscripts(String queryStr, String sort, LimitOffset limOff,
                                                          boolean sessionOnly) throws SearchException {
        return searchTranscripts(queryStr, null, sort, limOff, sessionOnly);
    }

    /**
     * Performs a search across all transcripts in a given year.
     *
     * @param queryStr Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s)
     * @param limOff Restrict the result set.
     */
    SearchResults<TranscriptId> searchTranscripts(String queryStr, Integer year, String sort, LimitOffset limOff,
                                                  boolean sessionOnly) throws SearchException;

    /**
     * Handles a transcript update event by indexing the supplied transcript.
     */
    void handleTranscriptUpdate(TranscriptUpdateEvent transcriptUpdateEvent);
}
