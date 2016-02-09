package gov.nysenate.openleg.dao.log.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public SearchResults<Integer> searchLogs(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder =
            getSearchRequest(logIndexName, query, filter, null, null, sort, limOff, false);
        SearchResponse response = searchBuilder.execute().actionGet();
        return getSearchResults(response, limOff, hit -> Integer.parseInt(hit.getId()));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<ApiLogItemView> searchLogsAndFetchData(QueryBuilder query, FilterBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder =
                getSearchRequest(logIndexName, query, filter, null, null, sort, limOff, true);
        SearchResponse response = searchBuilder.execute().actionGet();
        return getSearchResults(response, limOff,
            hit -> objectMapper.convertValue(hit.getSource(), ApiLogItemView.class));
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
            BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
            List<ApiLogItemView> logViewList = apiResponses.stream().map(ApiLogItemView::new).collect(Collectors.toList());
            logViewList.forEach(log ->
                bulkRequest.add(searchClient
                    .prepareIndex(logIndexName, "default", Integer.toString(log.getRequestId()))
                    .setSource(OutputUtils.toJson(log)))
                );
            safeBulkRequestExecute(bulkRequest);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteLogFromIndex(Integer requestId) {
        deleteEntry(logIndexName, "default", Integer.toString(requestId));
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Collections.singletonList(logIndexName);
    }
}
