package gov.nysenate.openleg.service.notification;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Category(SillyTest.class)
public class NotificationTest extends BaseTests {

    @Autowired
    private NotificationSubscriptionDataService subDataService;

    @Autowired
    private EventBus eventBus;

    @Test
    public void subscribeTest() {
        NotificationSubscription subscription = InstantNotificationSubscription.builder()
                .setUserName("stouffer@nysenate.gov")
                .setNotificationType(NotificationType.EXCEPTION)
                .setMedium(NotificationMedium.EMAIL)
                .setTargetAddress("stouffer@nysenate.gov")
                .setDetail(true)
                .setActive(true)
                .build();
        subDataService.updateSubscription(subscription);
    }

    @Test
    public void slackSubscribeTest() {
        NotificationSubscription subscription = InstantNotificationSubscription.builder()
                .setUserName("sam")
                .setNotificationType(NotificationType.EXCEPTION)
                .setMedium(NotificationMedium.SLACK)
                .setTargetAddress("samsto")
                .setDetail(true)
                .setActive(true)
                .build();
        subDataService.updateSubscription(subscription);
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
