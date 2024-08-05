package gov.nysenate.openleg.search.transcripts.session;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.SessionType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao<TranscriptView> implements TranscriptSearchDao {
    private static final Map<String, HighlightField> highlightedFields =
            Map.of("text", HighlightField.of(b -> b.numberOfFragments(3)));

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(Query query,
                                                         List<SortOptions> sort, LimitOffset limOff) {
        return search(query,
                highlightedFields, sort, limOff,
                false, tv -> new TranscriptId(LocalDateTime.parse(tv.getDateTime()), new SessionType(tv.getSessionType())));
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
                .map(transcript -> getIndexOperation(transcript.getId().toString(), new TranscriptView(transcript)))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(bulkBuilder);
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
