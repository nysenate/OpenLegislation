package gov.nysenate.openleg.dao.log.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class ElasticApiLogSearchDao extends ElasticBaseDao implements ApiLogSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchDao.class);

    private static final String logIndexName = SearchIndex.API_LOG.getIndexName();

    @Autowired protected ObjectMapper objectMapper;

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchLogs(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        return search(logIndexName, query, filter, sort, limOff, this::parseId);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<ApiLogItemView> searchLogsAndFetchData(QueryBuilder query, QueryBuilder filter, List<SortBuilder> sort, LimitOffset limOff) {
        return search(logIndexName,
                query, filter, null, null, sort, limOff, true, this::parseLogItem);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(ApiResponse apiResponse) {
        updateLogIndex(Collections.singletonList(apiResponse));
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(Collection<ApiResponse> apiResponses) {
        BulkRequest bulkRequest = new BulkRequest();
        apiResponses.stream()
                .map(ApiLogItemView::new)
                .map(log -> getJsonIndexRequest(logIndexName, Integer.toString(log.getRequestId()), log))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
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

    /**
     * Allocate additional shards for log index.
     *
     * @return Settings.Builder
     */
    @Override
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = super.getIndexSettings();
        indexSettings.put("index.number_of_shards", 8);
        return indexSettings;
    }

    @Override
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("requestMethod", ImmutableMap.of("type", "keyword"));
        props.put("ipAddress", ImmutableMap.of("type", "ip"));
        return props;
    }

    private Integer parseId(SearchHit hit) {
        return Integer.parseInt(hit.getId());
    }

    private ApiLogItemView parseLogItem(SearchHit hit) {
        return objectMapper.convertValue(hit.getSourceAsMap(), ApiLogItemView.class);
    }
}
