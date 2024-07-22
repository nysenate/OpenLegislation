package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao<Transcript> implements TranscriptSearchDao {
    private static final String transcriptIndexName = SearchIndex.TRANSCRIPT.getName();
    private static final Map<String, HighlightField> highlightedFields =
            Map.of("text", HighlightField.of(b -> b.numberOfFragments(3)));

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(Query query, Query postFilter,
                                                         List<SortOptions> sort, LimitOffset limOff) {
        return search(transcriptIndexName, query, postFilter,
                highlightedFields, null, sort, limOff,
                false, Transcript::getId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Transcript transcript) {
        updateTranscriptIndex(List.of(transcript));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Collection<Transcript> transcripts) {
        var bulkBuilder = new BulkOperation.Builder();
        transcripts.stream()
                .map(transcript -> getIndexOperationRequest(transcriptIndexName, transcript.getId().toString(), transcript))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(transcriptIndexName).operations(bulkBuilder.build())));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTranscriptFromIndex(TranscriptId transcriptId) {
        if (transcriptId != null) {
            deleteEntry(transcriptIndexName, transcriptId.dateTime().toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.TRANSCRIPT;
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("filename", searchableKeywordMapping,
                "location", searchableKeywordMapping,
                "sessionType", searchableKeywordMapping);
    }
}
