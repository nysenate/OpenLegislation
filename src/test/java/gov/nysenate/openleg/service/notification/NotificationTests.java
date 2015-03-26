package gov.nysenate.openleg.service.notification;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class NotificationTests extends BaseTests {

    @Autowired
    private NotificationSubscriptionDataService subDataService;

    @Autowired
    private EventBus eventBus;

    @Test
    public void subscribeTest() {
        NotificationSubscription subscription = new NotificationSubscription("stouffer@nysenate.gov", NotificationType.EXCEPTION,
                NotificationTarget.EMAIL, "stouffer@nysenate.gov");
        subDataService.insertSubscription(subscription);
    }

    @Test
    public void slackSubscribeTest() {
        NotificationSubscription subscription = new NotificationSubscription("sam", NotificationType.EXCEPTION,
                NotificationTarget.SLACK, "samsto");
        subDataService.insertSubscription(subscription);
    }

    @Test
    public void dispatchTest() {
        String summary = "uh oh";
        String message = "uh oh, christmas sobis are here";
        Notification processExceptionNotification = new Notification(NotificationType.PROCESS_EXCEPTION,
                LocalDateTime.of(2014, 12, 25, 0, 0), summary, message);
        eventBus.post(processExceptionNotification);
    }

    @Test
    public void alternateDispatchTest() {
        String summary = "ding!";
        String message = "spotcheck done. " + Math.random() * Integer.MAX_VALUE + " errors";
        Notification processExceptionNotification = new Notification(NotificationType.SPOTCHECK,
                LocalDateTime.of(2014, 12, 25, 0, 0), summary, message);
        eventBus.post(processExceptionNotification);
    }

}
