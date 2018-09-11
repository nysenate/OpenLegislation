package gov.nysenate.openleg.dao.base;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
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
import java.io.InputStream;
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

    private static final String refreshIntervalSetting = "refresh_interval";

    protected static final String defaultType = "_doc";

    private static final String COUNT_API = "/_cat/count/";

    @Autowired private RestHighLevelClient searchClient;

    @PostConstruct
    private void init() {
        createIndices();
    }

    /* --- Public methods --- */

    public void createIndices() {
        getIndices().stream()
                .filter(index -> !indicesExist(index))
                .forEach(this::createIndex);
    }

    public void purgeIndices() {
        getIndices().forEach(index -> {
            deleteIndex(index);
            createIndex(index);
        });
    }

    /* --- Abstract methods --- */

    /**
     * Returns a list containing the names of all indices used by the inheriting Dao
     */
    protected abstract List<String> getIndices();

    /* --- Common Elastic Search methods --- */

    /**
     * Performs a typical search that involves a query, filter, sort string, and a limit + offset
     *
     * @see #search(String, QueryBuilder, QueryBuilder, List, RescorerBuilder, List, LimitOffset, boolean, Function)
     * <p>
     * Highlighting, rescoring, and full source response are not supported via this method.
     */
    protected <T> SearchResults<T> search(String indexName, QueryBuilder query, QueryBuilder postFilter,
                                          List<SortBuilder> sort, LimitOffset limitOffset,
                                          Function<SearchHit, T> hitMapper)
            throws ElasticsearchException {
        return search(indexName, query, postFilter,
                null, null, sort, limitOffset, false, hitMapper);
    }


    /**
     * Performs a search with support for various functions.
     *
     * @param indexName         - The name of the index to search.
     * @param query             - The QueryBuilder instance to perform the search with.
     * @param postFilter        - Optional FilterBuilder to filter out the results.
     * @param highlightedFields - Optional list of field names to return as highlighted fields.
     * @param rescorer          - Optional rescorer that can be used to fine tune the query ranking.
     * @param sort              - List of SortBuilders specifying the desired sorting
     * @param limitOffset       - Restrict the number of results returned as well as paginate.
     * @param fetchSource       - Will return the indexed source fields when set to true.
     * @param hitMapper         - function for converting elastic results into the desired java objects.
     * @return SearchRequest
     */
    protected <T> SearchResults<T> search(String indexName,
                                          QueryBuilder query,
                                          QueryBuilder postFilter,
                                          List<HighlightBuilder.Field> highlightedFields,
                                          RescorerBuilder rescorer,
                                          List<SortBuilder> sort,
                                          LimitOffset limitOffset,
                                          boolean fetchSource,
                                          Function<SearchHit, T> hitMapper
    ) throws ElasticsearchException {
        if (indexIsEmpty(indexName)) {
            return SearchResults.empty();
        }
        SearchRequest searchRequest = getSearchRequest(
                indexName, query, postFilter, highlightedFields, rescorer, sort, limitOffset, fetchSource);
        SearchResponse searchResponse = getSearchResponse(searchRequest);
        return getSearchResults(searchResponse, limitOffset, hitMapper);
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
        GetRequest getRequest = new GetRequest(index, type, id);
        try {
            GetResponse getResponse = searchClient.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()){
                return Optional.of(responseMapper.apply(getResponse));
            }
        }
        catch(IOException ex){
            throw new ElasticsearchException("Get request failed.", ex);
        }
        return Optional.empty();
    }

    /**
     * Return a request to index the given object as a json document.
     *
     * @param indexName String
     * @param id String - elasticsearch id for the object
     * @param object - Object to be indexed.
     * @return IndexRequest
     */
    protected IndexRequest getJsonIndexRequest(String indexName, String id, Object object) {
        return new IndexRequest(indexName, defaultType, id)
                .source(OutputUtils.toElasticsearchJson(object), XContentType.JSON);
    }

    /**
     * Index the given object as a json document;
     *
     * @param indexName String
     * @param id String - elasticsearch id for the object.
     * @param object - Object to be indexed
     */
    protected IndexResponse indexJsonDoc(String indexName, String id, Object object) {
        try {
            IndexRequest jsonIndexRequest = getJsonIndexRequest(indexName, id, object);
            return searchClient.index(jsonIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            throw new ElasticsearchException("Index request failed", ex);
        }
    }

    /**
     * Performs a bulk request execution while making sure that the bulk request is actually valid to
     * prevent exceptions.
     * @param bulkRequest BulkRequestBuilder
     */
    protected void safeBulkRequestExecute(BulkRequest bulkRequest) {
        if (bulkRequest != null && bulkRequest.numberOfActions() > 0) {
            try {
                searchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
            catch (IOException ex){
                throw new ElasticsearchException("Bulk request failed", ex);
            }
        }
    }

    protected void deleteEntry(String indexName, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName)
                .type(defaultType)
                .id(id);
        try {
            searchClient.delete(deleteRequest, RequestOptions.DEFAULT);
        }
        catch (IOException ex){
            throw new ElasticsearchException("Delete request failed.", ex);
        }
    }

    /**
     * @return the max result window for the index, this can be overridden.
     */
    protected int getMaxResultWindow() {
        return defaultMaxResultWindow;
    }

    /**
     * Allows for enabling/disabling periodic refreshing for an index.
     *
     * Disabling can reduce load during large operations.
     * It should always be re-enabled when done.
     * @param indexName
     * @param enabled
     */
    protected void setIndexRefresh(String indexName, boolean enabled) {
        logger.info("{} index refresh for {}", enabled ? "Enabling" : "Disabling", indexName);
        Settings settings;
        if (enabled) {
            // Set to null to restore default setting
            settings = Settings.builder().putNull(refreshIntervalSetting).build();
        } else {
            settings = Settings.builder().put(refreshIntervalSetting, -1).build();
        }
        UpdateSettingsRequest request = new UpdateSettingsRequest(settings, indexName);
        // Try to set the setting up to 5 times if a failure occurs
        Throwable ex = null;
        for (int attempts = 0; attempts < 5; attempts++) {
            try {
                UpdateSettingsResponse response = searchClient.indices().putSettings(request, RequestOptions.DEFAULT);
                if (response.isAcknowledged()) {
                    return;
                }
            } catch (Exception e) {
                ex = e;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("Attempt to set index refresh interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new ElasticsearchException("Failed to set refresh setting to " + enabled + " for index " + indexName, ex);
    }

    /**
     * Returns true iff index refresh is the default value for the given index.
     *
     * @param indexName String
     * @return boolean
     */
    protected boolean isIndexRefreshDefault(String indexName) {
        GetSettingsResponse currentIndexSettings = getCurrentIndexSettings(indexName);
        String currentRefreshInterval = currentIndexSettings.getSetting(indexName, "index." + refreshIntervalSetting);
        return currentRefreshInterval == null;
    }

    /**
     * Generates default index settings.
     *
     * Can be overridden by implementations for custom settings.
     * @return Settings.Builder
     */
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = Settings.builder();
        indexSettings.put("index.max_result_window", getMaxResultWindow());
        // Disable replicas since we do not run multiple nodes
        indexSettings.put("index.number_of_replicas", 0);
        return indexSettings;
    }

    /* --- Internal Methods --- */

    private boolean indicesExist(String... indices) {
        GetIndexRequest getIndexRequest = new GetIndexRequest()
                .indices(indices);
        try {
            return searchClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        }
        catch (IOException ex){
            throw new ElasticsearchException("Exist request failed.", ex);
        }
    }

    private void createIndex(String indexName) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName, getIndexSettings().build());
        try {
            searchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        catch (IOException ex){
            throw new ElasticsearchException("Create index request failed.", ex);
        }
    }

    private void deleteIndex(String index) {
        try {
            logger.info("Deleting search index {}", index);
            searchClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        }
        catch (IndexNotFoundException ex) {
            logger.info("Cannot delete index {} because it doesn't exist.", index);
        }
        catch (IOException ex){
            throw new ElasticsearchException("Delete index request failed.", ex);
        }
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
     * @param fetchSource - Will return the indexed source fields when set to true.
     * @return SearchRequest
     */
    private SearchRequest getSearchRequest(String indexName,
                                           QueryBuilder query,
                                           QueryBuilder postFilter,
                                           List<HighlightBuilder.Field> highlightedFields,
                                           RescorerBuilder rescorer,
                                           List<SortBuilder> sort,
                                           LimitOffset limitOffset,
                                           boolean fetchSource) throws ElasticsearchException {
        limitOffset = adjustLimitOffset(limitOffset);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(query)
                .from(limitOffset.getOffsetStart() - 1)
                .size((limitOffset.hasLimit()) ? limitOffset.getLimit() : Integer.MAX_VALUE)
                .minScore(0.05f)
                .fetchSource(new FetchSourceContext(fetchSource));

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
     * Execute a search query, returning a response.
     * Handle IOExceptions by rethrowing as runtime exception.
     * @param request SearchRequest
     * @return SearchResponse
     */
    private SearchResponse getSearchResponse(SearchRequest request) throws ElasticsearchException {
        try {
            return searchClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            throw new ElasticsearchException("IOException occurred during search request.", ex);
        }
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
    private <R> SearchResults<R> getSearchResults(SearchResponse response, LimitOffset limitOffset,
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
     * Checks if an index is empty. If it is, it should not be searched, as trying causes errors.
     * @param indexName to check
     * @return if indexName is empty
     */
    private boolean indexIsEmpty(String indexName){
        try {
            InputStream responseStream = searchClient.getLowLevelClient()
                    .performRequest(new Request("GET", COUNT_API + indexName + "?v"))
                    .getEntity()
                    .getContent();
            byte[] isIndexEmpty = new byte[1];
            // Read past the unnecessary data to get to the count of documents in the specified index.
            // If the first byte of this number is the character 0, then the index must be empty.
            // See https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-count.html for details.
            if (responseStream.skip(48) != 48 || responseStream.read(isIndexEmpty) != 1) {
                throw new IOException();
            }
            return isIndexEmpty[0] == '0';
        }
        catch (IOException io){
            throw new ElasticsearchException("Error while executing search request.");
        }
    }

    /**
     * Get the current settings for the given index, including defaults.
     *
     * @param indexName String
     * @return GetSettingsResponse
     */
    private GetSettingsResponse getCurrentIndexSettings(String indexName) {
        GetSettingsRequest request = new GetSettingsRequest().indices(indexName);
        try {
            return searchClient.indices().getSettings(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException("Failed to get elasticsearch settings");
        }
    }
}
