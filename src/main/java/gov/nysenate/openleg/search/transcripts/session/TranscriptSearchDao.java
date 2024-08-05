package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.SearchResults;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching session transcripts.
 */
public interface TranscriptSearchDao {
    /**
     * Performs a free-form search across all transcripts using the query string syntax and a filter.
     * @param query  String - Query Builder
     * @param sort   String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<BillId>
     */
    SearchResults<TranscriptId> searchTranscripts(Query query,
                                                  List<SortOptions> sort, LimitOffset limOff);

    /**
     * Update the transcript search index with the supplied transcript.
     */
    void updateTranscriptIndex(Transcript transcript);

    /**
     * Updates the transcript search index with the supplied transcripts.
     */
    void updateTranscriptIndex(Collection<Transcript> transcripts);
}
