package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationType;

import java.util.List;
import java.util.Set;

public interface NotificationSubscriptionDataService {

    /**
     * Get all subscriptions for the given user name
     *
     * @param userName String
     * @return List<NotificationSubscription>
     */
    public Set<NotificationSubscription> getSubscriptions(String userName);

    /**
     * Get all subscriptions that cover notifications of the given type
     * @param type NotificationType
     * @return List<NotificationSubscription>
     */
    public Set<NotificationSubscription> getSubscriptions(NotificationType type);

    /**
     * Insert a new subscription
     * @param subscription NotificationSubscription
     */
    public void insertSubscription(NotificationSubscription subscription);

    /**
     * Remove a subscription
     * @param subscription NotificationSubscription
     */
    public void removeSubscription(NotificationSubscription subscription);
}
