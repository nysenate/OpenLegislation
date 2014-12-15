package gov.nysenate.openleg.service.transcript.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.event.BulkTranscriptUpdateEvent;
import gov.nysenate.openleg.service.transcript.event.TranscriptUpdateEvent;

public interface TranscriptSearchService
{
    /**
     * Provides a listing of all transcripts.
     * @see #searchTranscripts(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<TranscriptId> searchTranscripts(String sort, LimitOffset limOff) throws SearchException;

    /**
     * Provides a listing of transcripts which took place in a given year.
     * @see #searchTranscripts(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<TranscriptId> searchTranscripts(int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all transcripts.
     * @see #searchTranscripts(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<TranscriptId> searchTranscripts(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all transcripts in a given year.
     *
     * @param query Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s)
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    public SearchResults<TranscriptId> searchTranscripts(String query, int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Handles a transcript update event by indexing the supplied transcript.
     * @param transcriptUpdateEvent
     */
    public void handleTranscriptUpdate(TranscriptUpdateEvent transcriptUpdateEvent);

    /**
     * Handles a batch transcript update event by indexing the supplied transcripts.
     * @param bulkTranscriptUpdateEvent
     */
    public void handleBulkTranscriptUpdate(BulkTranscriptUpdateEvent bulkTranscriptUpdateEvent);

}
