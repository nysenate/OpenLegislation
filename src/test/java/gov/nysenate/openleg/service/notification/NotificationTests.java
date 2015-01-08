package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.NotificationType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class NotificationTests extends BaseTests {

    @Autowired
    private NotificationSubscriptionDataService subDataService;

    @Autowired
    private NotificationDispatcher notificationDispatcher;

    @Test
    public void subscribeTest() {
        NotificationSubscription subscription = new NotificationSubscription("sam", NotificationType.EXCEPTION,
                NotificationTarget.EMAIL, "stouffer@nysenate.gov");
        subDataService.insertSubscription(subscription);
    }

    @Test
    public void dispatchTest() {
        String summary = "uh oh";
        String message = "christmas sobis are here";
        Notification processExceptionNotification = new Notification(NotificationType.PROCESS_EXCEPTION,
                LocalDateTime.of(2014, 12, 25, 0, 0), summary, message);
        notificationDispatcher.dispatchNotification(processExceptionNotification);
    }

    @Test
    public void alternateDispatchTest() {
        String summary = "ding!";
        String message = "spotcheck done. " + Math.random() * Integer.MAX_VALUE + " errors";
        Notification processExceptionNotification = new Notification(NotificationType.SPOTCHECK,
                LocalDateTime.of(2014, 12, 25, 0, 0), summary, message);
        notificationDispatcher.dispatchNotification(processExceptionNotification);
    }

}
