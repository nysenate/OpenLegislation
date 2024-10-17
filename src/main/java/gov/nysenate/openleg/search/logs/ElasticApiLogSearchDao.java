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

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ElasticApiLogSearchDao extends ElasticBaseDao<Long, ApiLogItemView, ApiResponse> {
    private AtomicLong nextId;

    @PostConstruct
    private void init() {
        this.nextId = new AtomicLong(getDocCount() + 1);
    }

    @Override
    protected String getId(ApiResponse data) {
        data.getBaseRequest().setRequestId(nextId.getAndIncrement());
        return String.valueOf(data.getBaseRequest().getRequestId());
    }

    @Override
    protected ApiLogItemView getDoc(ApiResponse data) {
        return new ApiLogItemView(data);
    }

    /** {@inheritDoc} */
    @Override
    public SearchIndex indexType() {
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
