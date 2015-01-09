package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.model.notification.NotificationSubscription;

import java.util.List;
import java.util.Set;

public interface NotificationSubscriptionDao {

    /**
     * Retrieve all notification subscriptions
     * @return List<NotificationSubscription>
     */
    public Set<NotificationSubscription> getSubscriptions();

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
