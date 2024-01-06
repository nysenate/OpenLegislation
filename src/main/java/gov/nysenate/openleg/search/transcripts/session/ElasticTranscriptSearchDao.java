package gov.nysenate.openleg.search.transcripts.session;

import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao implements TranscriptSearchDao {
    private static final String transcriptIndexName = SearchIndex.TRANSCRIPT.getName();
    private static final List<HighlightBuilder.Field> highlightedFields =
            Collections.singletonList(new HighlightBuilder.Field("text").numOfFragments(3));
    private static final String idSeparator = "|**|";

    /** {@inheritDoc} */
    @Override
    public SearchResults<TranscriptId> searchTranscripts(QueryBuilder query, QueryBuilder postFilter,
                                                         List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(transcriptIndexName, query, postFilter,
                highlightedFields, null, sort, limOff,
                false, this::getTranscriptIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Transcript transcript) {
        updateTranscriptIndex(List.of(transcript));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Collection<Transcript> transcripts) {
        BulkRequest bulkRequest = new BulkRequest();
        transcripts.stream()
                .map(TranscriptView::new)
                .map(t -> getJsonIndexRequest(transcriptIndexName,
                        t.getDateTime() + idSeparator + t.getSessionType(), t))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
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
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("filename", searchableKeywordMapping);
        props.put("location", searchableKeywordMapping);
        props.put("sessionType", searchableKeywordMapping);
        return props;
    }

    private TranscriptId getTranscriptIdFromHit(SearchHit hit) {
        String[] data = hit.getId().split(Pattern.quote(idSeparator));
        return new TranscriptId(LocalDateTime.parse(data[0]), data[1]);
    }
}
