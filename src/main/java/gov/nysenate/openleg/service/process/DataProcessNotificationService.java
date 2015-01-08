package gov.nysenate.openleg.service.process;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.process.DataProcessErrorEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import static gov.nysenate.openleg.model.notification.NotificationType.*;

@Service
public class DataProcessNotificationService {

    @Autowired
    private EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /**
     * Generates and dispatches a data process exception notification based on an exception
     * @param ex Throwable
     */
    public void exceptionNotification(Throwable ex) {
        LocalDateTime occurred = LocalDateTime.now();
        String summary = ex.getMessage();
        String message = "The following exception occurred while processing data at " + occurred + "\n"
                         + ExceptionUtils.getStackTrace(ex);
        Notification notification = new Notification(PROCESS_EXCEPTION, occurred, summary, message);

        eventBus.post(notification);
    }

    @Subscribe
    public void handleDataProcessErrorEvent(DataProcessErrorEvent event) {
        exceptionNotification(event.getEx());
    }
}
