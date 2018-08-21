package gov.nysenate.openleg.dao.law.search;

import gov.nysenate.openleg.client.view.law.LawDocView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** {@inheritDoc} */
@Repository
public class ElasticLawSearchDao extends ElasticBaseDao implements LawSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticLawSearchDao.class);

    protected static String lawIndexName = SearchIndex.LAW.getIndexName();

    protected static List<HighlightBuilder.Field> highlightFields =
        Arrays.asList(new HighlightBuilder.Field("text").numOfFragments(5),
                      new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(QueryBuilder query, QueryBuilder postFilter,
                                                 RescorerBuilder rescorer, List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequest searchRequest =
            getSearchRequest(lawIndexName, query, postFilter, highlightFields, rescorer, sort, limOff, true);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchResponse = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.error("Search Law request failed.", ex);
        }

        return getSearchResults(searchResponse, limOff, this::getLawDocIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(LawDocument lawDoc) {
        if (lawDoc != null) {
            updateLawIndex(Collections.singletonList(lawDoc));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(Collection<LawDocument> lawDocs) {
        if (lawDocs != null && !lawDocs.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            lawDocs.stream().map(LawDocView::new).forEach(docView -> {
                bulkRequest.add(
                    new IndexRequest(lawIndexName, defaultType, docView.getLawId() + createSearchId(docView))
                                .source(OutputUtils.toElasticsearchJson(docView), XContentType.JSON));
            });
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLawDocFromIndex(LawDocId lawDocId) {
        if (lawDocId != null) {
            deleteEntry(lawIndexName, createSearchId(lawDocId));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Collections.singletonList(lawIndexName);
    }

    /** --- Internal --- */

    private LawDocId getLawDocIdFromHit(SearchHit hit) {
        String docId = hit.getId();
        return new LawDocId(docId, LocalDate.parse((String) hit.getSourceAsMap().get("activeDate")));
    }

    private String createSearchId(LawDocId lawDocId) {
        return lawDocId.getLocationId();
    }

    private String createSearchId(LawDocView lawDocView) {
        return lawDocView.getLocationId();
    }
}
