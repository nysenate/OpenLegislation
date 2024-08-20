package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import gov.nysenate.openleg.common.dao.LimitOffset;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Manages an index that maps an IdType to a ViewType, and indexes ContentType documents.
 */
public interface SearchDao<IdType, ViewType, ContentType> {
    SearchIndex indexType();

    String indexName();

    void ensureIndexExists();

    void purgeIndex();

    SearchResults<IdType> searchForIds(QueryVariant query, String sortStr, LimitOffset limOff) throws SearchException;

    default SearchResults<IdType> searchForIds(QueryVariant query, String queryStr, String sortStr, LimitOffset limOff) throws SearchException {
        return searchForIds(
                combineQueries(query, ElasticSearchServiceUtils.getStringQuery(queryStr)),
                sortStr, limOff);
    }

    SearchResults<ViewType> searchForDocs(QueryVariant query, String sortStr, LimitOffset limOff) throws SearchException;

    void updateIndex(ContentType data);

    void updateIndex(Collection<ContentType> data);

    void deleteFromIndex(IdType id);

    private static QueryVariant combineQueries(QueryVariant... queries) {
        return BoolQuery.of(b -> b.must(Arrays.stream(queries).filter(Objects::nonNull)
                .map(QueryVariant::_toQuery).toList()));
    }
}
