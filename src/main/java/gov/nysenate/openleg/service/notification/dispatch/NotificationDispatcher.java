package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.service.notification.data.NotificationService;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final Environment environment;
    private final NotificationService notificationService;
    private final NotificationSubscriptionDataService subDataService;

    private final ImmutableMap<NotificationMedium, NotificationSender> senderMap;

    /** Lock notif. dispatching to prevent race conditions involving 'last sent' subscription parameter. */
    private final ReentrantLock dispatchLock = new ReentrantLock();

    private static final Duration notificationGroupPeriod = Duration.ofMinutes(60);
    private final NotificationGroups notificationGroups;

    @Autowired
    public NotificationDispatcher(EventBus eventBus,
                                  Environment environment,
                                  NotificationService notificationService,
                                  NotificationSubscriptionDataService subDataService,
                                  List<NotificationSender> notificationSenders) {

        eventBus.register(this);
        this.senderMap = Maps.uniqueIndex(notificationSenders, NotificationSender::getTargetType);
        this.environment = environment;
        this.notificationService = notificationService;
        this.subDataService = subDataService;
        this.notificationGroups = new NotificationGroups(
                EnumSet.allOf(NotificationType.class).stream()
                .filter(NotificationType::shouldErrorGroup)
                .map(type -> new NotificationGroup(type, notificationGroupPeriod))
                .collect(Collectors.toMap(NotificationGroup::getNotificationType, Function.identity())));
    }

    /**
     * Sends a registered notification to all pertinent subscribers
     * @param notification NotificationBody
     */
    @Async
    public void dispatchNotification(RegisteredNotification notification) {
        if (!environment.isNotificationsEnabled()) {
            logger.info("Notifications Disabled. A notification event has occurred but will not be dispatched " +
                        "because notifications are disabled.");
            return;
        }
        dispatchLock.lock();

        try {
            // Group subscriptions by medium
            Multimap<NotificationMedium, NotificationSubscription> mediumToSubscriptions = ArrayListMultimap.create();
            for (NotificationSubscription sub : subDataService.getSubscriptions(notification.getNotificationType())) {
                mediumToSubscriptions.put(sub.getMedium(), sub);
            }

            // Send out all subscriptions for each medium.
            for (NotificationMedium medium : mediumToSubscriptions.keySet()) {
                NotificationSender sender = senderMap.get(medium);
                sender.sendNotification(notification, mediumToSubscriptions.get(medium));
            }

        } catch (Throwable ex) {
            handleNotificationException(ex);
        } finally {
            dispatchLock.unlock();
        }
    }

    @Subscribe
    public void handleNotificationEvent(Notification notification) {
        try {
            RegisteredNotification registeredNotification = notificationService.registerNotification(notification);
            notificationGroups.registerNotification(notification);
            if (notificationGroups.shouldDispatch(notification.getNotificationType())) {
                dispatchNotification(registeredNotification);
            }
        } catch (Exception ex) {
            handleNotificationException(ex);
        }
    }

    /* --- Internal Methods --- */

    /**
     * Handle an exception by logging it
     * All notification related exceptions should be caught by this method
     * To prevent an infinite feedback loop
     *
     * @param ex Throwable
     */
    private static void handleNotificationException(Throwable ex) {
        logger.error("Caught exception while dispatching notification!", ex);
    }
}
