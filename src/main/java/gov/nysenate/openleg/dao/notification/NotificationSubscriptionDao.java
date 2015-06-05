package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.model.notification.NotificationDigestSubscription;
import gov.nysenate.openleg.model.notification.NotificationSubscription;

import java.time.LocalDateTime;
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

    /**
     * Gets all notification digest subscriptions whose next digest is before the current time
     * @return Set<NotificationDigestSubscription>
     */
    public Set<NotificationDigestSubscription> getPendingDigests();

    /**
     * Gets all notification digest subscriptions for the given user
     * @param username String
     * @return Set<NotificationDigestSubscription>
     */
    public Set<NotificationDigestSubscription> getDigestSubsForUser(String username);

    /**
     * Inserts a new Notification Digest Subscription
     * @param subscription NotificationDigestSubscription
     */
    public void insertDigestSubscription(NotificationDigestSubscription subscription);

    /**
     * Updates the next digest field of the subscription
     * @param digestSubscriptionId int
     * @param nextDigest LocalDateTime
     */
    public void updateNextDigest(int digestSubscriptionId, LocalDateTime nextDigest);

    /**
     * Removes the digest subscription with the given id
     * @param digestSubscriptionId int
     */
    public void removeDigestSubscription(int digestSubscriptionId);
}
