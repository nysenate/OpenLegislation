package gov.nysenate.openleg.search.law;

import gov.nysenate.openleg.api.legislation.law.view.LawDocView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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

    protected static String lawIndexName = SearchIndex.LAW.getName();

    protected static List<HighlightBuilder.Field> highlightFields =
        Arrays.asList(new HighlightBuilder.Field("text").numOfFragments(5),
                      new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(QueryBuilder query, QueryBuilder postFilter,
                                                 RescorerBuilder<?> rescorer, List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(lawIndexName, query, postFilter,
                highlightFields, rescorer,
                sort, limOff,
                true, this::getLawDocIdFromHit);
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
            for (LawDocument doc : lawDocs) {
                String searchId = createSearchId(doc);
                LawDocView lawDocView = new LawDocView(doc);
                IndexRequest indexRequest = getJsonIndexRequest(lawIndexName, searchId, lawDocView);
                bulkRequest.add(indexRequest);
            }
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLawDocsFromIndex(Collection<LawDocId> lawDocIds) {
        BulkRequest bulkRequest = new BulkRequest();
        lawDocIds.stream()
                .map(docId -> getDeleteRequest(lawIndexName, createSearchId(docId)))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.LAW;
    }

    /**
     * Allocate additional shards for law index.
     *
     * @return Settings.Builder
     */
    @Override
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = super.getIndexSettings();
        indexSettings.put("index.number_of_shards", 2);
        return indexSettings;
    }

    /* --- Internal --- */

    private LawDocId getLawDocIdFromHit(SearchHit hit) {
        String docId = hit.getId();
        return new LawDocId(docId, LocalDate.parse((String) hit.getSourceAsMap().get("activeDate")));
    }

    private String createSearchId(LawDocId lawDocId) {
        return lawDocId.getDocumentId();
    }

}
