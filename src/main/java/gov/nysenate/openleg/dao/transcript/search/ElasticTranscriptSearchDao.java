package gov.nysenate.openleg.dao.transcript.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class ElasticTranscriptSearchDao extends ElasticBaseDao implements TranscriptSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticTranscriptSearchDao.class);

    protected static final String transcriptIndexName = SearchIndex.TRANSCRIPT.getIndexName();

    protected static final List<HighlightBuilder.Field> highlightedFields =
            Collections.singletonList(new HighlightBuilder.Field("text").numOfFragments(3));

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
        updateTranscriptIndex(Collections.singletonList(transcript));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Collection<Transcript> transcripts) {
        BulkRequest bulkRequest = new BulkRequest();
        transcripts.stream()
                .map(TranscriptView::new)
                .map(t -> getJsonIndexRequest(transcriptIndexName, t.getDateTime(), t))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTranscriptFromIndex(TranscriptId transcriptId) {
        if (transcriptId != null) {
            deleteEntry(transcriptIndexName, transcriptId.getDateTime().toString());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(transcriptIndexName);
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
        return new TranscriptId(hit.getId());
    }
}
