package gov.nysenate.openleg.dao.log.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ElasticApiLogSearchDao extends ElasticBaseDao implements ApiLogSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchDao.class);

    protected static final String logIndexName = SearchIndex.API_LOG.getIndexName();

    @Autowired protected ObjectMapper objectMapper;

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchLogs(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        SearchResponse response = justSearchLogs(query, filter, sort, limOff, false);
        return getSearchResults(response, limOff,
                hit -> Integer.parseInt(hit.getId()));
    }

    /**
     * Helper method to create a response when searching logs.
     */
    private SearchResponse justSearchLogs(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff, boolean isFetch){
        SearchRequest searchRequest =
                getSearchRequest(logIndexName, query, filter, null, null, sort, limOff, isFetch);
        try {
            return searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.error("Search API Logs request failed.", ex);
        }
        return new SearchResponse();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<ApiLogItemView> searchLogsAndFetchData(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        SearchResponse response = justSearchLogs(query, filter, sort, limOff, true);
        return getSearchResults(response, limOff,
            hit -> objectMapper.convertValue(hit.getSourceAsMap(), ApiLogItemView.class));
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(ApiResponse apiResponse) {
        updateLogIndex(Collections.singletonList(apiResponse));
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(Collection<ApiResponse> apiResponses) {
        if (!apiResponses.isEmpty()) {
            BulkRequest bulkRequest = new BulkRequest();
            List<ApiLogItemView> logViewList = apiResponses.stream().map(ApiLogItemView::new).collect(Collectors.toList());
            logViewList.forEach(log ->
                bulkRequest.add(new IndexRequest(logIndexName, defaultType, Integer.toString(log.getRequestId()))
                    .source(OutputUtils.toElasticsearchJson(log), XContentType.JSON))
                );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLogFromIndex(Integer requestId) {
        deleteEntry(logIndexName, Integer.toString(requestId));
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Collections.singletonList(logIndexName);
    }
}
