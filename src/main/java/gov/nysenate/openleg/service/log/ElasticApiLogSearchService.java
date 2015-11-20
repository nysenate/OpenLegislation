package gov.nysenate.openleg.service.log;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.log.data.ApiLogDao;
import gov.nysenate.openleg.dao.log.search.ElasticApiLogSearchDao;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.model.search.ClearIndexEvent;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ElasticApiLogSearchService implements ApiLogSearchService
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticApiLogSearchService.class);

    @Autowired private ApiLogDao apiLogDao;
    @Autowired private ElasticApiLogSearchDao apiLogSearchDao;

    @Override
    public void updateIndex(ApiResponse apiResponse) {
        apiLogSearchDao.updateLogIndex(apiResponse);
    }

    @Override
    public void updateIndex(Collection<ApiResponse> apiResponses) {
        apiLogSearchDao.updateLogIndex(apiResponses);
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