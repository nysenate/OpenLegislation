package gov.nysenate.openleg.service.log.data;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.service.log.event.ApiLogEvent;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.log.data.ApiLogDao;
import gov.nysenate.openleg.model.auth.ApiResponse;
import gov.nysenate.openleg.service.log.event.ApiLogIndexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlApiLogDataService implements ApiLogDataService
{
    private static final Logger logger = LoggerFactory.getLogger(SqlApiLogDataService.class);

    @Autowired protected EventBus eventBus;
    @Autowired protected ApiLogDao apiLogDao;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    @Async
    public void saveApiResponseAsync(ApiLogEvent apiLogEvent, boolean emitSearchEvent) {
        try {
            if (apiLogEvent != null) {
                ApiResponse apiResponse = apiLogEvent.getApiResponse();
                apiLogDao.saveApiResponse(apiResponse);
                if (emitSearchEvent) {
                    // This event should be picked up by the log indexer
                    eventBus.post(new ApiLogIndexEvent(apiResponse));
                }
            }
        } catch (DataAccessException ex) {
            logger.error("Error while saving api req/res log.", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ApiResponse> getResponses(Range<LocalDateTime> dateTimeRange, LimitOffset limOff, SortOrder order) {
        return apiLogDao.getResponses(dateTimeRange, limOff, order);
    }
}