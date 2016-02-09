package gov.nysenate.openleg.service.log.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.client.view.log.ApiLogItemView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.log.data.ApiLogDao;
import gov.nysenate.openleg.dao.log.search.ElasticApiLogSearchDao;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.ClearIndexEvent;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.log.event.ApiLogIndexEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Service
public class ElasticApiLogSearchService implements ApiLogSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchService.class);

    @Autowired private EventBus eventBus;
    @Autowired private ApiLogDao apiLogDao;
    @Autowired private ElasticApiLogSearchDao apiLogSearchDao;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
    }

    @Override
    public SearchResults<ApiLogItemView> searchApiLogs(String query, String sort, LimitOffset limOff) throws SearchException {
        try {
            return apiLogSearchDao.searchLogsAndFetchData(QueryBuilders.queryString(query), null,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string");
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!");
        }
    }

    @Override
    public void updateIndex(ApiResponse apiResponse) {
        apiLogSearchDao.updateLogIndex(apiResponse);
    }

    @Override
    public void updateIndex(Collection<ApiResponse> apiResponses) {
        apiLogSearchDao.updateLogIndex(apiResponses);
    }

    @Subscribe
    public void handleUpdateIndexEvent(ApiLogIndexEvent apiLogIndexEvent) {
        if (apiLogIndexEvent != null) {
            apiLogSearchDao.updateLogIndex(apiLogIndexEvent.getApiResponse());
        }
    }

    @Override
    public void clearIndex() {
        apiLogSearchDao.purgeIndices();
        apiLogSearchDao.createIndices();
    }

    @Override
    public void rebuildIndex() {
        clearIndex();
        LimitOffset limOff = LimitOffset.THOUSAND;
        List<ApiResponse> responses;
        while (!(responses = apiLogDao.getResponses(limOff, SortOrder.ASC)).isEmpty()) {
            logger.info("Indexing logs, batch {} - {}", limOff.getOffsetStart(), limOff.getOffsetEnd());
            apiLogSearchDao.updateLogIndex(responses);
            limOff = limOff.next();
        }
    }

    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.API_LOG)) {
            rebuildIndex();
        }
    }

    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.API_LOG)) {
            clearIndex();
        }
    }
}