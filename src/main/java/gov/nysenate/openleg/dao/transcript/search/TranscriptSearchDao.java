package gov.nysenate.openleg.dao.transcript.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import org.elasticsearch.index.query.FilterBuilder;
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
    public SearchResults<TranscriptId> searchTranscripts(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Update the transcript search index with the supplied transcript.
     * @param transcript
     */
    public void updateTranscriptIndex(Transcript transcript);

    /**
     * Updates the transcript search index with the supplied transcripts.
     * @param transcripts
     */
    public void updateTranscriptIndex(Collection<Transcript> transcripts);

    /**
     * Removes the transcript from the search index with the given id.
     *
     * @param transcriptId
     */
    public void deleteTranscriptFromIndex(TranscriptId transcriptId);
}
