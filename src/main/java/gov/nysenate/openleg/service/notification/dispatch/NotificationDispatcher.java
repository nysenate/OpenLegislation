package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.service.notification.data.NotificationService;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class NotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final Environment environment;
    private final NotificationService notificationService;
    private final NotificationSubscriptionDataService subscriptionDataService;

    private final ImmutableMap<NotificationMedium, NotificationSender> senderMap;

    /** Lock notif. dispatching to prevent race conditions involving 'last sent' subscription parameter. */
    private final ReentrantLock dispatchLock = new ReentrantLock();

    @Autowired
    public NotificationDispatcher(EventBus eventBus,
                                  Environment environment,
                                  NotificationService notificationService,
                                  NotificationSubscriptionDataService subscriptionDataService,
                                  List<NotificationSender> notificationSenders) {

        eventBus.register(this);
        this.senderMap = Maps.uniqueIndex(notificationSenders, NotificationSender::getTargetType);

        this.environment = environment;
        this.notificationService = notificationService;
        this.subscriptionDataService = subscriptionDataService;
    }

    /**
     * Sends a registered notification to all pertinent subscribers
     * @param notification NotificationBody
     */
    @Async
    public void dispatchNotification(RegisteredNotification notification) {
        if (!environment.isNotificationsEnabled()) {
            return;
        }
        dispatchLock.lock();
        try {
            Multimap<NotificationMedium, NotificationSubscription> instantSubMap = ArrayListMultimap.create();
            List<NotificationSubscription> rateLimitedSubscription = new ArrayList<>();
            // sort out digest and instant subscriptions
            for (NotificationSubscription subscription :
                    subscriptionDataService.getSubscriptions(notification.getNotificationType())) {
                // Don't check Scheduled Notifications, its not time to send them even if they are subscribed to this notification type.
                if (subscription.getSubscriptionType() == NotificationSubscriptionType.INSTANT) {
                    if (subscription.receivesDigests()) {
                        // If its an InstantSubscription with a rate limit
                        if (subscription.canDispatchNow()) {
                            // If we are outside the rate limit - we can send a notification.
                            rateLimitedSubscription.add(subscription);
                        }
                    } else {
                        instantSubMap.put(subscription.getMedium(), subscription);
                    }
                }
            }

            // Send the instant notifications for each medium
            instantSubMap.keySet().forEach(target ->
                    senderMap.get(target).sendNotification(notification, instantSubMap.get(target)));
            // Set last sent values for instant notifications
            instantSubMap.values()
                    .forEach(sub -> subscriptionDataService.setLastSent(sub.getId(), notification.getOccurred()));


            // Send digests to rate limited subscriptions
            for (NotificationSubscription rateLimitedSub : rateLimitedSubscription) {
                dispatchDigest(rateLimitedSub, notification);
                subscriptionDataService.setLastSent(rateLimitedSub.getId(), LocalDateTime.now());
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
            dispatchNotification(registeredNotification);
        } catch (Exception ex) {
            handleNotificationException(ex);
        }
    }

    /**
     * Sends Digests to Scheduled Notifications and Instant Notifications if they are ready to receive notifications.
     *
     * For Instant Notifications, this ensures that Notifications which were withheld due to the rateLimit are eventually sent.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void processPendingSubscriptions() {
        dispatchLock.lock();
        try {
            Set<NotificationSubscription> pendingSubscriptions = subscriptionDataService.getAllSubscriptions().stream()
                    .filter(NotificationSubscription::canDispatchNow).collect(Collectors.toSet());
            if (pendingSubscriptions.isEmpty()) {
                return;
            }
            logger.info("processing {} pending notification subscriptions..", pendingSubscriptions.size());
            for (NotificationSubscription pendingSubscription : pendingSubscriptions) {
                LocalDateTime dispatchTime = LocalDateTime.now();
                dispatchDigest(pendingSubscription, null);
                subscriptionDataService.setLastSent(pendingSubscription.getId(), dispatchTime);
            }
            logger.info("Done sending notification subscriptions");
        } catch (Exception ex) {
            handleNotificationException(ex);
        } finally {
            dispatchLock.unlock();
        }
    }

    /* --- Internal Methods --- */

    private void dispatchDigest(NotificationSubscription subscription, RegisteredNotification newNotification)
            throws SearchException {
        NotificationDigest digest = makeDigest(subscription, newNotification);

        if (subscription.sendEmpty() || !digest.isEmpty()) {
            logger.info("sending {} to {}:{}", NotificationDigestFormatter.getSummary(digest),
                    digest.getMedium(), digest.getAddress());
            senderMap.get(subscription.getMedium()).sendDigest(digest);
        }
    }

    /**
     * Create a {@link NotificationDigest} using a subscription and a new notification.
     *
     * (The new notification is used if this call was triggered by a new notification,
     * in which case the new notification may not be available in the data store.)
     * @param subscription {@link NotificationSubscription}
     * @param newNotif {@link RegisteredNotification}
     * @return {@link NotificationDigest}
     * @throws SearchException if something goes bad
     */
    private NotificationDigest makeDigest(NotificationSubscription subscription,
                                          @Nullable RegisteredNotification newNotif) throws SearchException {
        Optional<RegisteredNotification> newNotifOpt = Optional.ofNullable(newNotif);

        LocalDateTime toDateTime = newNotifOpt.map(Notification::getOccurred).orElse(LocalDateTime.now());

        Range<LocalDateTime> digestRange = Range.open(subscription.getDigestStartTime(), toDateTime);

        List<RegisteredNotification> notifications = new ArrayList<>(
                notificationService.getNotificationList(
                        Collections.singleton(subscription.getNotificationType()),
                        digestRange, SortOrder.ASC, LimitOffset.ALL)
                        .getResults()
        );
        newNotifOpt.ifPresent(notifications::add);

        return new NotificationDigest(subscription.getNotificationType(),
                subscription.getDigestStartTime(),
                toDateTime,
                notifications,
                subscription.isDetail(),
                subscription.getMedium(),
                subscription.getTargetAddress());
    }

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
