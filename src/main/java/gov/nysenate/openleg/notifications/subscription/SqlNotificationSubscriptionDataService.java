package gov.nysenate.openleg.notifications.subscription;

import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.SubscriptionNotFoundEx;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SqlNotificationSubscriptionDataService
        implements NotificationSubscriptionDataService {

    @Autowired
    private NotificationSubscriptionDao subscriptionDao;

    /* --- NotificationSubscriptionDataService Implementation --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(String userName) {
        return subscriptionDao.getSubscriptions().stream()
                .filter(subscription -> StringUtils.equals(userName, subscription.getUserName()))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(NotificationType type) {
        return subscriptionDao.getSubscriptions().stream()
                .filter(s -> s.subscribesTo(type))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationSubscription updateSubscription(NotificationSubscription subscription) {
        return subscriptionDao.updateSubscription(subscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSubscription(int subscriptionId) {
        subscriptionDao.removeSubscription(subscriptionId);
    }

    /** {@inheritDoc} */
    @Override
    public void setActive(int subscriptionId, boolean active) throws SubscriptionNotFoundEx {
        NotificationSubscription subscription = subscriptionDao.getSubscription(subscriptionId);
        NotificationSubscription modified = subscription.copy().setActive(active).build();
        subscriptionDao.updateSubscription(modified);
    }
}
