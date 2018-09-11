package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.service.notification.data.NotificationService;
import gov.nysenate.openleg.service.notification.subscription.NotificationSubscriptionDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Environment environment;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationSubscriptionDataService subscriptionDataService;

    @Autowired
    private List<NotificationSender> notificationSenders;

    private ImmutableMap<NotificationTarget, NotificationSender> senderMap;

    @PostConstruct
    public void init() {
        Map<NotificationTarget, NotificationSender> senderProtoMap = new HashMap<>();
        notificationSenders.forEach(sender -> senderProtoMap.put(sender.getTargetType(), sender));
        senderMap = ImmutableMap.copyOf(senderProtoMap);

        eventBus.register(this);
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
        try {
            Multimap<NotificationTarget, NotificationSubscription> subscriptionMap = ArrayListMultimap.create();
            subscriptionDataService.getSubscriptions(notification.getNotificationType())
                    .forEach(subscription -> addSubscription(subscriptionMap, subscription));

            subscriptionMap.keySet().forEach(target ->
                    senderMap.get(target).sendNotification(notification, subscriptionMap.get(target)));
        } catch (Throwable ex) {
            handleNotificationException(ex);
        }
    }

    @Subscribe
    public void handleNotificationEvent(Notification notification) {
        try {
            RegisteredNotification registeredNotification = notificationService.registerNotification(notification);
            dispatchNotification(registeredNotification);
        } catch (Throwable ex) {
            handleNotificationException(ex);
        }
    }

    /* --- Internal Methods --- */

    /**
     * Adds a subscription to the specified multimap
     * if the map does not already contain a subscription with the same address and same target
     * ensuring that a person won't get the same notification multiple times
     */
    private void addSubscription(Multimap<NotificationTarget, NotificationSubscription> subMap,
                                                        NotificationSubscription subscription) {
        for (NotificationSubscription existingSub : subMap.get(subscription.getTarget())) {
            if(StringUtils.equals(existingSub.getTargetAddress(), subscription.getTargetAddress())) {
                return;
            }
        }
        subMap.put(subscription.getTarget(), subscription);
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
