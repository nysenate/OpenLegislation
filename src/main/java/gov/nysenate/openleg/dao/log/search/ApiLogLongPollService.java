package gov.nysenate.openleg.dao.log.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.service.log.event.ApiLogEvent;
import gov.nysenate.openleg.util.OutputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApiLogLongPollService
{
    private static final Logger logger = LoggerFactory.getLogger(ApiLogLongPollService.class);

    /**
     * Ignore the longPoll api call because otherwise we would always return the api request for the
     * long poll request itself.
     */
    private static String IGNORED_API_PATH = "apiLogs/longPoll";

    @Service
    public static class AsyncLogPoller
    {
        List<DeferredResult<Object>> deferredList = Collections.synchronizedList(new ArrayList<>());

        @Async
        public void addDeferred(DeferredResult<Object> deferredResult) {
            if (!deferredResult.isSetOrExpired()) {
                deferredList.add(deferredResult);
            }
        }

        @Async
        public void processLogEvent(ApiLogEvent apiLogEvent) {
            deferredList.forEach(deferredResult -> {
                if (!deferredResult.isSetOrExpired()) {
                    deferredResult.setResult(OutputUtils.toJson(apiLogEvent));
                }
            });
            deferredList.clear();
        }
    }

    @Autowired private EventBus eventBus;
    @Autowired private AsyncLogPoller poller;

    @PostConstruct
    private void init() {
        this.eventBus.register(this);
    }

    public void addDeferred(DeferredResult<Object> deferredResult) {
        poller.addDeferred(deferredResult);
    }

    @Subscribe
    public void handleApiLogEvent(ApiLogEvent apiLogEvent) {
        if (apiLogEvent != null && apiLogEvent.getApiResponse() != null) {
            if (!apiLogEvent.getApiResponse().getBaseRequest().getUrl().contains(IGNORED_API_PATH)) {
                poller.processLogEvent(apiLogEvent);
            }
        }
    }
}
