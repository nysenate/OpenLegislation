package gov.nysenate.openleg.service.log.search;

import com.google.common.collect.Range;
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
import gov.nysenate.openleg.util.AsyncUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ElasticApiLogSearchService implements ApiLogSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchService.class);

    @Autowired private EventBus eventBus;
    @Autowired private ApiLogDao apiLogDao;
    @Autowired private ElasticApiLogSearchDao apiLogSearchDao;
    @Autowired private AsyncUtils asyncUtils;

    private static final int logReindexThreadCount = 4;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
    }

    @Override
    public SearchResults<ApiLogItemView> searchApiLogs(String query, String sort, LimitOffset limOff) throws SearchException {
        try {
            return apiLogSearchDao.searchLogsAndFetchData(QueryBuilders.queryStringQuery(query), null,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException(ex.getMessage(), ex);
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

        LocalDate earliestLogDay;
        List<ApiResponse> responses = apiLogDao.getResponses(LimitOffset.ONE, SortOrder.ASC);
        if (responses.isEmpty()) {
            logger.info("No logs to index");
            return;
        } else {
            earliestLogDay = responses.get(0).getResponseDateTime().toLocalDate();
        }

        LinkedBlockingQueue<LocalDate> logDayQueue = new LinkedBlockingQueue<>();
        for (LocalDate day = earliestLogDay; !day.isAfter(LocalDate.now()); day = day.plusDays(1)) {
            logDayQueue.add(day);
        }

        // Initialize and run several BillReindexWorkers to index bills from the queue
        CompletableFuture[] futures = new CompletableFuture[logReindexThreadCount];
        AtomicBoolean interrupted = new AtomicBoolean(false);
        for (int workerNo = 0; workerNo < logReindexThreadCount; workerNo++) {
            futures[workerNo] = asyncUtils.run(new LogReindexWorker(logDayQueue, interrupted));
        }
        CompletableFuture.allOf(futures).join();
        logger.info("Finished api log reindex.");
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

    /**
     * Runnable that loads and indexes logs based on a queue of days
     */
    private class LogReindexWorker implements Runnable {

        /** Job queue of days of logs to be indexed */
        private final BlockingQueue<LocalDate> dayQueue;
        /** Flag shared between workers that is set to true if one worker experiences an error */
        private final AtomicBoolean interrupted;

        LogReindexWorker(BlockingQueue<LocalDate> dayQueue, AtomicBoolean interrupted) {
            this.dayQueue = dayQueue;
            this.interrupted = interrupted;
        }

        @Override
        public void run() {
            try {
                LocalDate day;
                while ((day = dayQueue.poll()) != null){
                    if (interrupted.get()) {
                        logger.info("Terminating reindex job due to exception in other worker");
                        return;
                    }

                    logger.info("Indexing logs for {}", day);
                    Range<LocalDateTime> range = Range.closedOpen(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
                    final LimitOffset limitOffset = LimitOffset.THOUSAND;
                    List<ApiResponse> responses;
                    int indexed = 0;
                    // Iterate using request time instead of offset, utilizing request time index.
                    while (!(responses = apiLogDao.getResponses(range, limitOffset, SortOrder.ASC)).isEmpty()) {
                        apiLogSearchDao.updateLogIndex(responses);
                        indexed += responses.size();
                        ApiResponse lastResponse = responses.get(responses.size() - 1);
                        LocalDateTime lastRequestTime = lastResponse.getBaseRequest().getRequestTime();
                        range = Range.open(lastRequestTime, day.plusDays(1).atStartOfDay());
                    }
                    logger.info("Finished indexing logs for {} ({} total)", day, indexed);
                }
            } catch (Throwable ex) {
                logger.error("Error while indexing logs {}", ex.getMessage());
                // Set the interrupted flag if an exception occurs
                interrupted.set(true);
                throw ex;
            } finally {
                logger.info("log reindex worker terminating.");
            }
        }
    }
}