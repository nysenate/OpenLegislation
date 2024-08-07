package gov.nysenate.openleg.search.logs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.logs.ApiLogEvent;
import gov.nysenate.openleg.api.logs.ApiLogItemView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class ElasticApiLogSearchService implements ApiLogSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchService.class);

    private final SearchDao<Integer, ApiLogItemView, ApiResponse> apiLogSearchDao;

    private final BlockingQueue<ApiResponse> indexQueue = new ArrayBlockingQueue<>(50000);

    @Autowired
    public ElasticApiLogSearchService(SearchDao<Integer, ApiLogItemView, ApiResponse> apiLogSearchDao,
                                      EventBus eventBus) {
        this.apiLogSearchDao = apiLogSearchDao;
        eventBus.register(this);
    }

    @Override
    public SearchResults<ApiLogItemView> searchApiLogs(String query, String sort, LimitOffset limOff) throws SearchException {
        return apiLogSearchDao.searchForDocs(IndexedSearchService.getStringQuery(query),
                ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
    }

    @Override
    public void updateIndex(ApiResponse apiResponse) {
        apiLogSearchDao.updateIndex(apiResponse);
    }

    @Override
    public void updateIndex(Collection<ApiResponse> apiResponses) {
        apiLogSearchDao.updateIndex(apiResponses);
    }

    @Override
    public void clearIndex() {
        apiLogSearchDao.purgeIndices();
        apiLogSearchDao.createIndices();
    }

    @Override
    public void rebuildIndex() {
        throw new IllegalStateException("Cannot rebuild log search index.");
    }

    @Scheduled(cron = "${scheduler.log.index:* * * * * *}")
    public void indexQueuedLogs() {
        List<ApiResponse> responses = new ArrayList<>(1000);
        indexQueue.drainTo(responses);
        if (responses.size() > 1000) {
            logger.warn("More than 1000 requests queued for indexing: ({})", responses.size());
        }
        updateIndex(responses);
    }

    /**
     * The log event is handled here by putting the response in a queue to be indexed.
     * @param apiLogEvent ApiLogEvent
     */
    @Subscribe
    public void handleApiLogEvent(ApiLogEvent apiLogEvent) {
        try {
            this.indexQueue.put(apiLogEvent.getApiResponse());
        } catch (InterruptedException e) {
            e.printStackTrace();
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
