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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationDispatcher {

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
        if (environment.isNotificationsEnabled()) {
            Multimap<NotificationTarget, NotificationSubscription> subscriptionMap = ArrayListMultimap.create();
            subscriptionDataService.getSubscriptions(notification.getType())
                    .forEach(subscription -> addSubscription(subscriptionMap, subscription));

            subscriptionMap.keySet().forEach(target ->
                    senderMap.get(target).sendNotification(notification, subscriptionMap.get(target)));
        }
    }

    @Subscribe
    public void handleNotificationEvent(Notification notification) {
        RegisteredNotification registeredNotification = notificationService.registerNotification(notification);
        dispatchNotification(registeredNotification);
    }

    /** --- Internal Methods --- */

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
}
