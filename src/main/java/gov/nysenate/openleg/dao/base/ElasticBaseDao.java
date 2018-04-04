package gov.nysenate.openleg.dao.base;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for Elastic Search layer classes to inherit common functionality from.
 */
public abstract class ElasticBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBaseDao.class);

    private static final int defaultMaxResultWindow = 10000;

    @Autowired
    protected Client searchClient;

    @PostConstruct
    private void init() {
        createIndices();
    }

    /** --- Public methods --- */

    public void createIndices() {
        getIndices().stream()
                .filter(index -> !indicesExist(index))
                .forEach(this::createIndex);
    }

    public void purgeIndices() {
        getIndices().forEach(this::deleteIndex);
    }

    /** --- Abstract methods --- */

    /**
     * Returns a list containing the names of all indices used by the inheriting Dao
     *
     * @return
     */
    protected abstract List<String> getIndices();

    /** --- Common Elastic Search methods --- */

    /**
     * Generates a typical search request that involves a query, filter, sort string, and a limit + offset
     * @see #getSearchRequest(String, QueryBuilder, QueryBuilder, List, LimitOffset)
     *
     * Highlighting, rescoring, and full source response are not supported via this method.
     */
    protected SearchRequestBuilder getSearchRequest(String indexName, QueryBuilder query, QueryBuilder postFilter,
                                                    List<SortBuilder> sort, LimitOffset limitOffset) {
        return getSearchRequest(indexName, query, postFilter, null, null, sort, limitOffset, false);
    }

    /**
     * Generates a SearchRequest with support for various functions.
     *
     * @param indexName - The name of the index to search.
     * @param query - The QueryBuilder instance to perform the search with.
     * @param postFilter - Optional FilterBuilder to filter out the results.
     * @param highlightedFields - Optional list of field names to return as highlighted fields.
     * @param rescorer - Optional rescorer that can be used to fine tune the query ranking.
     * @param sort - List of SortBuilders specifying the desired sorting
     * @param limitOffset - Restrict the number of results returned as well as paginate.
     * @param fetchSource - Will return the indexed source fields when set to true
     * @return SearchRequestBuilder
     */
    protected SearchRequestBuilder getSearchRequest(String indexName, QueryBuilder query, QueryBuilder postFilter,
                                                    List<HighlightBuilder.Field> highlightedFields, RescoreBuilder.Rescorer rescorer,
                                                    List<SortBuilder> sort, LimitOffset limitOffset, boolean fetchSource) {
        limitOffset = adjustLimitOffset(limitOffset);
        SearchRequestBuilder searchBuilder = searchClient.prepareSearch(indexName)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(query)
                .setRescorer(rescorer)
                .setFrom(limitOffset.getOffsetStart() - 1)
                .setSize((limitOffset.hasLimit()) ? limitOffset.getLimit() : Integer.MAX_VALUE)
                .setMinScore(0.05f)
                .setFetchSource(fetchSource);
        if (highlightedFields != null) {
            highlightedFields.stream().forEach(searchBuilder::addHighlightedField);
        }
//        if (rescorer != null) {
//            searchBuilder.addRescorer(rescorer);
//        }
        // Post filters take effect after the search is completed
        if (postFilter != null) {
            searchBuilder.setPostFilter(postFilter);
        }
        // Add the sort by fields
        sort.forEach(searchBuilder::addSort);
        logger.debug("{}", searchBuilder);
        return searchBuilder;
    }

    /**
     * Extracts search results from a search response
     *
     * template <R> is the desired return type
     *
     * @param response a SearchResponse generated by a SearchRequest
     * @param limitOffset the LimitOffset used in the SearchRequest
     * @param hitMapper a function that maps a SearchHit to the desired return type R
     * @return SearchResults<R>
     */
    protected <R> SearchResults<R> getSearchResults(SearchResponse response, LimitOffset limitOffset,
                                                    Function<SearchHit, R> hitMapper) {
        limitOffset = adjustLimitOffset(limitOffset);
        List<SearchResult<R>> resultList = new ArrayList<>();
        for (SearchHit hit : response.getHits().hits()) {
            SearchResult<R> result = new SearchResult<>(
                    hitMapper.apply(hit), // Result
                    (!Float.isNaN(hit.getScore())) ? BigDecimal.valueOf(hit.getScore()) : BigDecimal.ONE, // Rank
                    hit.getHighlightFields()); // Highlights
            resultList.add(result);
        }
        return new SearchResults<>(Ints.checkedCast(response.getHits().getTotalHits()), resultList, limitOffset);
    }

    /**
     * Performs a get request on the given index for the document designated by the given type and id
     * returns an optional that is empty if a document does not exist for the given request parameters
     * @param index String - a search index
     * @param type String - a search type
     * @param id String - the id of the desired document
     * @param responseMapper Function<GetResponse, T> - a function that maps the response to the desired class
     * @param <T> The type to be returned
     * @return Optional<T></T>
     */
    protected <T> Optional<T> getRequest(String index, String type, String id, Function<GetResponse, T> responseMapper) {
        GetResponse getResponse = searchClient.prepareGet(index, type, id).execute().actionGet();
        if (getResponse.isExists()) {
            return Optional.of(responseMapper.apply(getResponse));
        }
        return Optional.empty();
    }

    /**
     * Performs a bulk request execution while making sure that the bulk request is actually valid to
     * prevent exceptions.
     * @param bulkRequest BulkRequestBuilder
     */
    protected void safeBulkRequestExecute(BulkRequestBuilder bulkRequest) {
        if (bulkRequest != null && bulkRequest.numberOfActions() > 0) {
            bulkRequest.execute().actionGet();
        }
    }

    protected void deleteEntry(String indexName, String type, String id) {
        DeleteRequestBuilder request = searchClient.prepareDelete();
        request.setIndex(indexName);
        request.setType(type);
        request.setId(id);
        request.execute().actionGet();

    }

    protected boolean indicesExist(String... indices) {
        return searchClient.admin().indices().exists(new IndicesExistsRequest(indices)).actionGet().isExists();
    }

    protected void createIndex(String indexName) {
        searchClient.admin().indices()
                .prepareCreate(indexName)
                .setSettings(getIndexSettings())
                .execute().actionGet();
    }

    /**
     * Get settings for index.
     * Use default settings by default.
     * Override for index specific settings.
     * @return Settings.Builder
     */
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = Settings.builder();
        indexSettings.put("index.max_result_window", getMaxResultWindow());
        return indexSettings;
    }

    /**
     * @return the max result window for the index, this can be overridden.
     */
    protected int getMaxResultWindow() {
        return defaultMaxResultWindow;
    }

    protected void deleteIndex(String index) {
        try {
            logger.info("Deleting search index {}", index);
            searchClient.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
        }
        catch (IndexNotFoundException ex) {
            logger.info("Cannot delete index {} because it doesn't exist.", index);
        }
    }

    /**
     * Validate and adjust limit offset so that it conforms to the index max result window.
     */
    private LimitOffset adjustLimitOffset(LimitOffset limitOffset) {
        final int maxResultWindow = getMaxResultWindow();

        if (!limitOffset.hasLimit() || limitOffset.getLimit() > maxResultWindow) {
            limitOffset = new LimitOffset(maxResultWindow, limitOffset.getOffsetStart());
        }

        if (limitOffset.getOffsetEnd() > maxResultWindow) {
            throw new IllegalArgumentException("LimitOffset with offset end of " + limitOffset.getOffsetEnd() +
                    " extends past allowed result window of " + maxResultWindow);
        }

        return limitOffset;
    }
}
