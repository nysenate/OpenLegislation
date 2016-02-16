package gov.nysenate.openleg.dao.law.search;

import gov.nysenate.openleg.client.view.law.LawDocView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
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
    public SearchResults<LawDocId> searchLawDocs(QueryBuilder query, FilterBuilder postFilter,
                                                 RescoreBuilder.Rescorer rescorer, List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder =
            getSearchRequest(lawIndexName, query, postFilter, highlightFields, rescorer, sort, limOff, true);
        SearchResponse response = searchBuilder.execute().actionGet();
        return getSearchResults(response, limOff, this::getLawDocIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(LawDocument lawDoc) {
        if (lawDoc != null) {
            updateLawIndex(Arrays.asList(lawDoc));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(Collection<LawDocument> lawDocs) {
        if (lawDocs != null && !lawDocs.isEmpty()) {
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            lawDocs.stream().map(doc -> new LawDocView(doc)).forEach(docView -> {
                bulkRequest.add(
                    searchClient.prepareIndex(lawIndexName, docView.getLawId(), createSearchId(docView))
                                .setSource(OutputUtils.toJson(docView)));
            });
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLawDocFromIndex(LawDocId lawDocId) {
        if (lawDocId != null) {
            deleteEntry(lawIndexName, lawDocId.getLawId(), createSearchId(lawDocId));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Arrays.asList(lawIndexName);
    }

    /** --- Internal --- */

    private LawDocId getLawDocIdFromHit(SearchHit hit) {
        String locationId = hit.getId();
        String docId = hit.getType() + locationId;
        return new LawDocId(docId, LocalDate.parse((String) hit.getSource().get("activeDate")));
    }

    private String createSearchId(LawDocId lawDocId) {
        return lawDocId.getLocationId();
    }

    private String createSearchId(LawDocView lawDocView) {
        return lawDocView.getLocationId();
    }
}
