package gov.nysenate.openleg.notifications.subscription;

import com.google.common.collect.Maps;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.SubscriptionNotFoundEx;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CachedNotificationSubscriptionDataService
        extends CachingService<String, Map<Integer, NotificationSubscription>>
        implements NotificationSubscriptionDataService {
    private static final String subCacheKey = "sUbCaChE";
    @SuppressWarnings("all")
    private static final Class<Map<Integer, NotificationSubscription>> VALUE_CLASS =
            (Class<Map<Integer, NotificationSubscription>>) Map.of(0, new NotificationSubscription.Builder().build())
                    .getClass();
    @Autowired
    private NotificationSubscriptionDao subscriptionDao;

    /* --- NotificationSubscriptionDataService Implementation --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(String userName) {
        return getSubscriptionMap().values().stream()
                .filter(subscription -> StringUtils.equals(userName, subscription.getUserName()))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(NotificationType type) {
        return getSubscriptionMap().values().stream()
                .filter(s -> s.subscribesTo(type))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    public Set<NotificationSubscription> getAllSubscriptions() {
        return new HashSet<>(getSubscriptionMap().values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationSubscription updateSubscription(NotificationSubscription subscription) {
        NotificationSubscription updated = subscriptionDao.updateSubscription(subscription);
        updateCachedSubscription(updated);
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSubscription(int subscriptionId) {
        subscriptionDao.removeSubscription(subscriptionId);
        getSubscriptionMap().remove(subscriptionId);
    }

    /** {@inheritDoc} */
    @Override
    public void setActive(int subscriptionId, boolean active) throws SubscriptionNotFoundEx {
        NotificationSubscription subscription = subscriptionDao.getSubscription(subscriptionId);
        NotificationSubscription modified = subscription.copy().setActive(active).build();
        NotificationSubscription updated = subscriptionDao.updateSubscription(modified);
        updateCachedSubscription(updated);
    }

    /* --- CachingService Implementations --- */

    @Override
    protected CacheType cacheType() {
        return CacheType.NOTIFICATION;
    }

    @Override
    protected Tuple<Class<String>, Class<Map<Integer, NotificationSubscription>>> getGenericClasses() {
        return new Tuple<>(String.class, VALUE_CLASS);
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(String s) {
        evictCaches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warmCaches() {
        getSubscriptionMap();
    }

    /** --- Internal Methods --- */

    private Map<Integer, NotificationSubscription> getSubscriptionMap() {
        var map = cache.get(subCacheKey);
        if (map != null)
            return map;
        Set<NotificationSubscription> subscriptionSet = subscriptionDao.getSubscriptions();
        Map<Integer, NotificationSubscription> subMap =
                new HashMap<>(Maps.uniqueIndex(subscriptionSet, NotificationSubscription::getId));
        cache.put(subCacheKey, subMap);
        return subMap;
    }

    private void updateCachedSubscription(NotificationSubscription newSub) {
        getSubscriptionMap().put(newSub.getId(), newSub);
    }
}
