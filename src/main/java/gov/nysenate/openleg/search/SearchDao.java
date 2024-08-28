package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.NonNullList;

import java.util.*;

/**
 * Manages an index that maps an IdType to a ViewType, and indexes ContentType documents.
 */
public interface SearchDao<IdType, ViewType, ContentType> {
    SearchIndex indexType();

    String indexName();

    /**
     * Creates the relevant index.
     * @return true if the index was created, false if it already existed.
     */
    boolean createIndex();

    void deleteIndex();

    SearchResults<IdType> searchForIds(QueryVariant query, String sortStr, LimitOffset limOff) throws SearchException;

    default SearchResults<IdType> searchForIds(String queryStr, String sortStr, LimitOffset limOff,
                                               QueryVariant... queries) throws SearchException {
        // Functionally treats null QueryVariants as a match_all query.
        var queryList = NonNullList.of(queries);
        queryList.addIfNotNull(ElasticSearchServiceUtils.getStringQuery(queryStr));
        QueryVariant finalQuery = BoolQuery.of(b -> b.must(queryList.stream().map(QueryVariant::_toQuery).toList()));
        return searchForIds(finalQuery, sortStr, limOff);
    }

    SearchResults<ViewType> searchForDocs(QueryVariant query, String sortStr, LimitOffset limOff) throws SearchException;

    void updateIndex(ContentType data);

    void updateIndex(Collection<ContentType> data);

    void deleteFromIndex(IdType id);
}
