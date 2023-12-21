package gov.nysenate.openleg.search.logs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.logs.ApiLogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ApiLogStompService {
    @Service
    static class AsyncApiLogStomper {
        private final SimpMessagingTemplate messagingTemplate;

        @Autowired
        public AsyncApiLogStomper(SimpMessagingTemplate messagingTemplate) {
            this.messagingTemplate = messagingTemplate;
        }

        @Async
        public void broadcast(ApiLogEvent apiLogEvent) {
            messagingTemplate.convertAndSend("/event/apiLogs", apiLogEvent);
        }
    }

    private final EventBus eventBus;
    private final AsyncApiLogStomper asyncStomper;

    @Autowired
    public ApiLogStompService(EventBus eventBus, AsyncApiLogStomper asyncStomper) {
        this.eventBus = eventBus;
        this.asyncStomper = asyncStomper;
    }

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
