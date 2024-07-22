package gov.nysenate.openleg.search.law;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Rescore;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.*;

/** {@inheritDoc} */
@Repository
public class ElasticLawSearchDao extends ElasticBaseDao<LawDocument> implements LawSearchDao {
    private static final String lawIndexName = SearchIndex.LAW.getName();
    private static final Map<String, HighlightField> highlightFields = Map.of(
            "text", HighlightField.of(b -> b.numberOfFragments(5)),
            "title", HighlightField.of(b -> b.numberOfFragments(0))
    );

    /** {@inheritDoc} */
    @Override
    public SearchResults<LawDocId> searchLawDocs(Query query, Query postFilter,
                                                 Rescore rescorer, List<SortOptions> sort, LimitOffset limOff) {
        return search(lawIndexName, query, postFilter,
                highlightFields, rescorer, sort, limOff,
                true, lawDoc -> new LawDocId(lawDoc.getDocumentId(), lawDoc.getPublishedDate()));
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(LawDocument lawDoc) {
        if (lawDoc != null) {
            updateLawIndex(List.of(lawDoc));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawIndex(Collection<LawDocument> lawDocs) {
        if (lawDocs != null && !lawDocs.isEmpty()) {
            var bulkBuilder = new BulkOperation.Builder();
            lawDocs.stream().map(lawDoc -> getIndexOperationRequest(lawIndexName, lawDoc.getLawId(), lawDoc))
                    .forEach(bulkBuilder::index);
            safeBulkRequestExecute(BulkRequest.of(b -> b.index(lawIndexName).operations(bulkBuilder.build())));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLawDocsFromIndex(Collection<LawDocId> lawDocIds) {
        var bulkBuilder = new BulkOperation.Builder();
        lawDocIds.stream()
                .map(docId -> DeleteOperation.of(b -> b.index(lawIndexName).id(docId.toString())))
                .forEach(bulkBuilder::delete);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(lawIndexName).operations(bulkBuilder.build())));
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
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("2");
    }
}
