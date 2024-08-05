package gov.nysenate.openleg.search.logs;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.IpProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.logs.ApiLogItemView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Repository
public class ElasticApiLogSearchDao extends ElasticBaseDao<ApiLogItemView> implements ApiLogSearchDao {

    /** {@inheritDoc} */
    @Override
    public SearchResults<ApiLogItemView> searchLogsAndFetchData(Query query, Query filter, List<SortOptions> sort, LimitOffset limOff) {
        return search(
                query, null, sort, limOff, true, Function.identity());
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(ApiResponse apiResponse) {
        var logItemView = new ApiLogItemView(apiResponse);
        indexDoc(String.valueOf(logItemView.getRequestId()), logItemView);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLogIndex(Collection<ApiResponse> apiResponses) {
        apiResponses.forEach(this::updateLogIndex);
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.API_LOG;
    }

    /**
     * Allocate additional shards for log index.
     *
     * @return Settings.Builder
     */
    @Override
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("8");
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("requestMethod", KeywordProperty.of(b -> b)._toProperty(),
                "ipAddress", IpProperty.of(b -> b)._toProperty());
    }
}
