package gov.nysenate.openleg.dao.transcript.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.transcript.TranscriptView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
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
import java.util.List;
import java.util.stream.Collectors;

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
                                                         List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequest searchRequest = getSearchRequest(transcriptIndexName, query, postFilter,
                highlightedFields, null, sort, limOff, null, false);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchResponse = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.warn("Search Transcript request failed.", ex);
        }

        logger.debug("Transcript search result with query {} and filter {} took {} ms", query, postFilter, searchResponse.getTook().getMillis());
        return getSearchResults(searchResponse, limOff, this::getTranscriptIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Transcript transcript) {
        updateTranscriptIndex(Collections.singletonList(transcript));
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscriptIndex(Collection<Transcript> transcripts) {
        if (!transcripts.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            List<TranscriptView> transcriptViewList = transcripts.stream().map(TranscriptView::new).collect(Collectors.toList());
            transcriptViewList.forEach(t ->
                            bulkRequest.add(
                                    new IndexRequest(transcriptIndexName, defaultType, t.getFilename())
                                            .source(OutputUtils.toJson(t), XContentType.JSON))
            );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTranscriptFromIndex(TranscriptId transcriptId) {
        if (transcriptId != null) {
            deleteEntry(transcriptIndexName, transcriptId.getFilename());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(transcriptIndexName);
    }

    private TranscriptId getTranscriptIdFromHit(SearchHit hit) {
        return new TranscriptId(hit.getId());
    }
}
