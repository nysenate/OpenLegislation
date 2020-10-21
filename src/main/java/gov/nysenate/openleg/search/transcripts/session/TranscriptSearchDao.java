package gov.nysenate.openleg.search.transcripts.session;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching session transcripts.
 */
public interface TranscriptSearchDao
{

    /**
     * Performs a free-form search across all transcripts using the query string syntax and a filter.
     *
     * @param query String - Query Builder
     * @param filter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<TranscriptId> searchTranscripts(QueryBuilder query, QueryBuilder filter, List<SortBuilder<?>> sort, LimitOffset limOff);

    /**
     * Update the transcript search index with the supplied transcript.
     * @param transcript
     */
    void updateTranscriptIndex(Transcript transcript);

    /**
     * Updates the transcript search index with the supplied transcripts.
     * @param transcripts
     */
    void updateTranscriptIndex(Collection<Transcript> transcripts);

    /**
     * Removes the transcript from the search index with the given id.
     *
     * @param transcriptId
     */
    void deleteTranscriptFromIndex(TranscriptId transcriptId);
}
