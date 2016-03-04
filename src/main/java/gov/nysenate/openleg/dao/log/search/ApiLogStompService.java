package gov.nysenate.openleg.dao.log.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.service.log.event.ApiLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ApiLogStompService
{
    private static final Logger logger = LoggerFactory.getLogger(ApiLogStompService.class);

    @Service
    public static class AsyncApiLogStomper
    {
        @Autowired private SimpMessagingTemplate messagingTemplate;

        private String brokerName = "/event/apiLogs";

        @Async
        public void broadcast(ApiLogEvent apiLogEvent) {
            messagingTemplate.convertAndSend(brokerName, apiLogEvent);
        }
    }

    @Autowired private EventBus eventBus;
    @Autowired private AsyncApiLogStomper asyncStomper;

    @PostConstruct
    private void init() {
        this.eventBus.register(this);
    }

    @Subscribe
    public void handleApiLogEvent(ApiLogEvent apiLogEvent) {
        if (apiLogEvent != null && apiLogEvent.getApiResponse() != null) {
            asyncStomper.broadcast(apiLogEvent);
        }
    }
}