package gov.nysenate.openleg.search.law;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import gov.nysenate.openleg.api.legislation.law.view.LawDocView;
import gov.nysenate.openleg.legislation.law.LawDocId;
import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class ElasticLawSearchDao extends ElasticBaseDao<LawDocId, LawDocView, LawDocument> {
    public void deleteLawDocsFromIndex(Collection<LawDocId> lawDocIds) {
        safeBulkRequestExecute(
                lawDocIds.stream()
                        .map(docId -> DeleteOperation.of(b -> b.index(indexName()).id(docId.toString())))
                        .map(delOp -> BulkOperation.of(b -> b.delete(delOp))).toList()
        );
    }

    /** {@inheritDoc} */
    @Override
    public SearchIndex indexType() {
        return SearchIndex.LAW;
    }

    @Override
    protected String getId(LawDocument data) {
        return new LawDocId(data).toString();
    }

    @Override
    protected LawDocView getDoc(LawDocument data) {
        return new LawDocView(data);
    }

    @Override
    protected LawDocId toId(String idStr) {
        String[] parts = idStr.split(":");
        return new LawDocId(parts[0], LocalDate.parse(parts[1]));
    }

    @Override
    protected Map<String, HighlightField> highlightedFields() {
        return Map.of(
                "text", HighlightField.of(b -> b.numberOfFragments(5)),
                "title", HighlightField.of(b -> b.numberOfFragments(0))
        );
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
