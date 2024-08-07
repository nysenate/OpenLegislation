package gov.nysenate.openleg.search.logs;

import co.elastic.clients.elasticsearch._types.mapping.IpProperty;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.api.logs.ApiLogItemView;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

@Repository
public class ElasticApiLogSearchDao extends ElasticBaseDao<Integer, ApiLogItemView, ApiResponse> {
    @Override
    protected Integer getId(ApiResponse data) {
        return data.getBaseRequest().getRequestId();
    }

    @Override
    protected ApiLogItemView getDoc(ApiResponse data) {
        return new ApiLogItemView(data);
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
