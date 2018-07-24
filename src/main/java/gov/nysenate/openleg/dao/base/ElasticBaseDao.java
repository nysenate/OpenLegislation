package gov.nysenate.openleg.dao.base;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    protected static final String defaultType = "_doc";

    @Autowired
    protected RestHighLevelClient searchClient;

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
     */
    protected abstract List<String> getIndices();

    /** --- Common Elastic Search methods --- */

    /**
     * Generates a typical search request that involves a query, filter, sort string, and a limit + offset
     * @see #getSearchRequest(String, QueryBuilder, QueryBuilder, List, LimitOffset, String[])
     *
     * Highlighting, rescoring, and full source response are not supported via this method.
     */
    protected SearchRequest getSearchRequest(String indexName, QueryBuilder query, QueryBuilder postFilter,
                                                    List<SortBuilder> sort, LimitOffset limitOffset, String[] filteredFields) {
        return getSearchRequest(indexName, query, postFilter, null, null, sort, limitOffset, filteredFields, false);
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
     * @param filteredFields - Optional List of fields to store in the response.
     * @param fetchFullSource - Will return the indexed source fields when set to true.
     * @return SearchRequest
     */
    protected SearchRequest getSearchRequest(String indexName, QueryBuilder query, QueryBuilder postFilter,
                                                    List<HighlightBuilder.Field> highlightedFields, RescorerBuilder rescorer,
                                                    List<SortBuilder> sort, LimitOffset limitOffset, String[] filteredFields, boolean fetchFullSource) {


        limitOffset = adjustLimitOffset(limitOffset);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(query)
                .from(limitOffset.getOffsetStart() - 1)
                .size((limitOffset.hasLimit()) ? limitOffset.getLimit() : Integer.MAX_VALUE)
                .minScore(0.05f)
                .fetchSource(new FetchSourceContext(fetchFullSource || filteredFields != null,
                        filteredFields, null));

        if (highlightedFields != null) {
            HighlightBuilder hb = new HighlightBuilder();
            highlightedFields.forEach(hb::field);
            searchSourceBuilder.highlighter(hb);
        }
        if (rescorer != null) {
            searchSourceBuilder.addRescorer(rescorer);
        }
        // Post filters take effect after the search is completed
        if (postFilter != null) {
            searchSourceBuilder.postFilter(postFilter);
        }
        // Add the sort by fields
        sort.forEach(searchSourceBuilder::sort);
        SearchRequest searchRequest = Requests.searchRequest(indexName)
                .source(searchSourceBuilder)
                .searchType(SearchType.QUERY_THEN_FETCH);
        logger.debug("{}", searchRequest);
        return searchRequest;
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
        for (SearchHit hit : response.getHits().getHits()) {
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
        GetRequest getRequest = new GetRequest()
                .index(index)
                .type(type)
                .id(id);
        try {
            GetResponse getResponse = searchClient.get(getRequest);
            if (getResponse.isExists()){
                return Optional.of(responseMapper.apply(getResponse));
            }
        }
        catch(IOException ex){
            logger.warn("Get request failed.", ex);
        }
        return Optional.empty();
    }

    /**
     * Performs a bulk request execution while making sure that the bulk request is actually valid to
     * prevent exceptions.
     * @param bulkRequest BulkRequestBuilder
     */
    protected void safeBulkRequestExecute(BulkRequest bulkRequest) {
        if (bulkRequest != null && bulkRequest.numberOfActions() > 0) {
            try {
                searchClient.bulk(bulkRequest);
            }
            catch (IOException ex){
                logger.warn("Bulk request failed.", ex);
            }
        }
    }

    protected void deleteEntry(String indexName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName)
        .type(defaultType)
        .id(id);
        try {
            searchClient.delete(deleteRequest);
        }
        catch (IOException ex){
            logger.warn("Delete request failed.", ex);
        }
    }

    protected boolean indicesExist(String... indices) {
        GetIndexRequest getIndexRequest = new GetIndexRequest()
                .indices(indices);
        try {
            return searchClient.indices().exists(getIndexRequest);
        }
        catch (IOException ex){
            logger.warn("Exist request failed.", ex);
        }
        return false;
    }

    protected void createIndex(String indexName) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName, getIndexSettings().build());
        try {
            searchClient.indices().create(createIndexRequest);
        }
        catch (IOException ex){
            logger.warn("Create index request failed.", ex);
        }
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
            searchClient.indices().delete(new DeleteIndexRequest(index));
        }
        catch (IndexNotFoundException ex) {
            logger.info("Cannot delete index {} because it doesn't exist.", index);
        }
        catch (IOException ex){
            logger.warn("Delete index request failed.", ex);
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

    /**
     * Returns the proper FetchSourceContext, based on fields to filter by and
     */
    private FetchSourceContext createFetchSourceContext(String[] filterFields, boolean fetchFullSource){
        if (fetchFullSource){
            return new FetchSourceContext(true);
        }
        if (filterFields == null){
            return new FetchSourceContext(false);
        }
        return new FetchSourceContext(true, filterFields, null);
    }
}
