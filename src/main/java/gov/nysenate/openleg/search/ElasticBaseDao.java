package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.SearchType;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.mapping.DateProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * Base class for Elasticsearch layer classes to inherit common functionality from.
 */
public abstract class ElasticBaseDao<DocType extends ViewObject> {
    private static final Logger logger = LoggerFactory.getLogger(ElasticBaseDao.class);
    private static final int defaultMaxResultWindow = 10000;

    @Autowired private ElasticsearchClient searchClient;

    @PostConstruct
    private void init() {
        createIndices();
    }

    /* --- Public methods --- */

    public void createIndices() {
        try {
            if (!searchClient.indices().exists(
                    ExistsRequest.of(b -> b.index(getIndex().getName()))).value()) {
                createIndex();
            }
        }
        catch (IOException ex) {
            throw new GenericElasticsearchException("Index exists request failed.", ex);
        }
    }

    public void purgeIndices() {
        try {
            logger.info("Deleting search index {}", getIndex());
            searchClient.indices().delete(DeleteIndexRequest.of(b -> b.index(getIndex().getName())));
        }
        catch (IOException ex) {
            throw new GenericElasticsearchException("Delete index request failed.", ex);
        }
        createIndex();
    }

    protected abstract SearchIndex getIndex();

    /* --- Common Elastic Search methods --- */

    protected long getDocCount() throws IOException {
        return searchClient.count(b -> b.index(getIndex().getName())).count();
    }

    /**
     * Performs a typical search that involves a query, filter, sort string, and a limit + offset
     *
     * @see #search(Query, List, LimitOffset, Function)
     * <p>
     * Highlighting, rescoring, and full source response are not supported via this method.
     */
    protected <T> SearchResults<T> search(Query query,
                                          List<SortOptions> sort, LimitOffset limitOffset,
                                          Function<DocType, T> docToReturnType)
            throws ElasticsearchException {
        return search(query,
                null, sort, limitOffset, false, docToReturnType);
    }

    /**
     * Performs a search with support for various functions.
     *
     * @param query             - The QueryBuilder instance to perform the search with.
     * @param highlightedFields - Optional list of field names to return as highlighted fields.
     * @param sort              - List of SortBuilders specifying the desired sorting
     * @param limitOffset       - Restrict the number of results returned as well as paginate.
     * @param fetchSource       - Will return the indexed source fields when set to true.
     * @return SearchRequest
     */
    protected <T> SearchResults<T> search(Query query,
                                          Map<String, HighlightField> highlightedFields,
                                          List<SortOptions> sort,
                                          LimitOffset limitOffset, boolean fetchSource,
                                          Function<DocType, T> docToReturnType) {
        // TODO: check searches on empty indices
        SearchRequest request = getSearchRequest(query, highlightedFields, sort, limitOffset, fetchSource);
        try {
            SearchResponse<DocType> response = searchClient.search(request, getDocTypeClass());
            return getSearchResults(response, limitOffset, docToReturnType);
        } catch (IOException ex) {
            throw new GenericElasticsearchException("IOException occurred during search request.", ex);
        }
    }

    /**
     * Performs a get request on the given index for the document designated by the given type and id
     * returns an optional that is empty if a document does not exist for the given request parameters
     * @param id String - the id of the desired document
     */
    protected Optional<DocType> getRequest(String id) {
        var getRequest = GetRequest.of(b -> b.index(getIndex().getName()).id(id));
        try {
            GetResponse<DocType> getResponse = searchClient.get(getRequest, getDocTypeClass());
            if (getResponse.found()) {
                return Optional.ofNullable(getResponse.source());
            }
        }
        catch (IOException ex) {
            throw new GenericElasticsearchException("Get request failed.", ex);
        }
        return Optional.empty();
    }

    protected IndexOperation<DocType> getIndexOperation(String id, DocType doc) {
        return IndexOperation.of(b -> b.index(getIndex().getName()).id(id).document(doc));
    }

    /**
     * Indexes the given document.
     *
     * @param id  String - elasticsearch ID for the document.
     * @param doc - Document to be indexed
     */
    protected void indexDoc(String id, DocType doc) {
        try {
            searchClient.index(IndexRequest.of(b -> b.index(getIndex().getName()).id(id).document(doc)));
        } catch (IOException ex) {
            throw new GenericElasticsearchException("Index request failed", ex);
        }
    }

    /**
     * Performs a bulk request execution while making sure that the bulk request is actually valid to
     * prevent exceptions.
     */
    protected void safeBulkRequestExecute(@Nonnull BulkOperation.Builder operationsBuilder) {
        try {
            searchClient.bulk(BulkRequest.of(b -> b.index(getIndex().getName()).operations(operationsBuilder.build())));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new GenericElasticsearchException("Bulk request failed", ex);
        }
    }

    protected void deleteEntry(String id) {
        DeleteRequest deleteRequest = DeleteRequest.of(b -> b.index(getIndex().getName()).id(id));
        try {
            searchClient.delete(deleteRequest);
        }
        catch (IOException ex) {
            throw new GenericElasticsearchException("Delete request failed.", ex);
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
     * Disabling can reduce load during large operations.
     * It should always be re-enabled when done.
     */
    protected void setIndexRefresh(boolean enabled) {
        logger.info("{} index refresh for {}", enabled ? "Enabling" : "Disabling", getIndex().getName());
        var request = PutIndicesSettingsRequest.of(b -> b.index(getIndex().getName()).settings(
                IndexSettings.of(b1 -> b1.refreshInterval(enabled ? enable : disable))
        ));
        // Try to set the setting up to 5 times if a failure occurs
        Throwable ex = null;
        for (int attempts = 0; attempts < 5; attempts++) {
            try {
                var response = searchClient.indices().putSettings(request);
                if (response.acknowledged()) {
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
        throw new GenericElasticsearchException("Failed to set refresh setting to " + enabled + " for index " + getIndex().getName(), ex);
    }

    /**
     * Returns true iff index refresh is the default value for this index.
     * @return boolean
     */
    @SuppressWarnings("all")
    public boolean isIndexRefreshDefault() {
        var request = GetIndicesSettingsRequest.of(b -> b.index(getIndex().getName()).includeDefaults(true));
        try {
            // TODO: are both null, not sure that they should be
            IndexState data = searchClient.indices().getSettings(request).get(getIndex().getName());
            var time1 = data.defaults().refreshInterval();
            var time2 = data.settings().refreshInterval();
            return Objects.equals(time1, time2);
        } catch (IOException e) {
            throw new GenericElasticsearchException("Failed to get elasticsearch settings");
        }
    }

    /**
     * Generates default index settings.
     * <p>
     * Can be overridden by implementations for custom settings.
     *
     * @return Settings.Builder
     */
    protected IndexSettings.Builder getIndexSettings() {
        // Disable replicas since we do not run multiple nodes
        return new IndexSettings.Builder().maxResultWindow(getMaxResultWindow())
                .numberOfReplicas("0").numberOfShards("1");
    }

    /**
     * Gets any custom mappings for the index.
     * <p>
     * Must be overridden to include any mappings.
     *
     * @return XContentBuilder
     */
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of();
    }

    /**
     * Ensures that any changes to indices are actually show, which is usually done automatically once per second.
     */
    public void refreshIndex() throws IOException {
        searchClient.indices().refresh(RefreshRequest.of(b -> b.index(getIndex().getName())));
    }

    /**
     * Custom mapping for a field that is primarily a keyword, but is also indexed as a text field for searching.
     */
    protected static final Property searchableKeywordMapping =
            KeywordProperty.of(b -> b.fields("text",
                    TextProperty.of(textB -> textB)._toProperty()
            ))._toProperty();

    protected static final Property basicTimeMapping =
            DateProperty.of(b -> b.format("hour_minute"))._toProperty();

    private static final Time disable = Time.of(b -> b.time("-1s")),
            enable = Time.of(b -> b.time("1s"));

    /* --- Internal Methods --- */

    @SuppressWarnings("unchecked")
    private Class<DocType> getDocTypeClass() {
        return (Class<DocType>) TypeUtils.getGenericTypes(this)[0];
    }

    private void createIndex() {
        try {
            var createIndexRequest = CreateIndexRequest.of(b -> b.index(getIndex().getName())
                    .settings(getIndexSettings().build())
                    .mappings(pb -> pb.properties(getCustomMappingProperties())));
            searchClient.indices().create(createIndexRequest);
        }
        catch (IOException ex) {
            throw new GenericElasticsearchException("Create index request failed.", ex);
        }
    }

    /**
     * Generates a SearchRequest with support for various functions.
     *
     * @param query - The QueryBuilder instance to perform the search with.
     * @param highlightedFields - Optional list of field names to return as highlighted fields.
     * @param sorts - List of SortBuilders specifying the desired sorting
     * @param limitOffset - Restrict the number of results returned as well as paginate.
     * @param fetchSource - Will return the indexed source fields when set to true.
     * @return SearchRequest
     */
    private SearchRequest getSearchRequest(Query query,
                                           Map<String, HighlightField> highlightedFields,
                                           List<SortOptions> sorts,
                                           LimitOffset limitOffset, boolean fetchSource)
            throws ElasticsearchException {
        final LimitOffset finalLimitOffset = adjustLimitOffset(limitOffset);
        var searchRequest = SearchRequest.of(b -> b.query(query).from(finalLimitOffset.getOffsetStart() - 1)
                .size(limitOffset.hasLimit() ? limitOffset.getLimit() : Integer.MAX_VALUE)
                .minScore(0.05d).trackTotalHits(TrackHits.of(trackBuilder -> trackBuilder.enabled(true)))
                .source(fetchBuilder -> fetchBuilder.fetch(fetchSource))
                .highlight(highlightBuilder -> highlightBuilder.fields(highlightedFields))
                .sort(sorts)
                .index(getIndex().getName())
                .searchType(SearchType.QueryThenFetch)
        );
        logger.debug("{}", searchRequest);
        return searchRequest;
    }

    /**
     * Extracts search results from a search response
     * template <R> is the desired return type
     *
     * @param response a SearchResponse generated by a SearchRequest
     * @param limitOffset the LimitOffset used in the SearchRequest
     * @return SearchResults<R>
     */
    private <T> SearchResults<T> getSearchResults(SearchResponse<DocType> response, LimitOffset limitOffset, Function<DocType, T> docToReturnType) {
        limitOffset = adjustLimitOffset(limitOffset);
        List<SearchResult<T>> results = response.hits().hits().stream().map(hit -> {
            double score = hit.score() != null && !Double.isNaN(hit.score()) ? hit.score() : 1;
            T hitValue = docToReturnType.apply(hit.source());
            return new SearchResult<>(
                    hitValue, BigDecimal.valueOf(score), hit.highlight());
        }).toList();
        if (response.hits().total() == null) {
            throw new GenericElasticsearchException("Problem checking total hits.");
        }
        return new SearchResults<>(Ints.checkedCast(response.hits().total().value()), results, limitOffset);
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
            throw new InvalidSearchParamException("LimitOffset with offset end of " + limitOffset.getOffsetEnd() +
                    " extends past allowed result window of " + maxResultWindow);
        }

        return limitOffset;
    }
}
