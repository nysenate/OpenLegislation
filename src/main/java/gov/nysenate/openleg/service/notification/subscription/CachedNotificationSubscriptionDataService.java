package gov.nysenate.openleg.service.notification.subscription;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.notification.NotificationSubscriptionDao;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.SubscriptionNotFoundEx;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CachedNotificationSubscriptionDataService implements NotificationSubscriptionDataService, CachingService
{

    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;
    @Value("${notification.cache.heap.size}") private long notificationCacheSizeMb;

    @Autowired
    private NotificationSubscriptionDao subscriptionDao;

    private Cache subCache;

    private static final String subCacheKey = "sUbCaChE";

    @PostConstruct
    private void init() {
        setupCaches();
        eventBus.register(this);
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.NOTIFICATION_SUBSCRIPTION.name());
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupCaches() {
        subCache = new Cache(new CacheConfiguration().name(ContentCache.NOTIFICATION_SUBSCRIPTION.name())
                .eternal(true)
                .maxBytesLocalHeap(notificationCacheSizeMb, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(byteSizeOfPolicy()));
        cacheManager.addCache(subCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ehcache> getCaches() {
        return Collections.singletonList(subCache);
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.NOTIFICATION_SUBSCRIPTION)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.NOTIFICATION_SUBSCRIPTION)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(Object o) {
        evictCaches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warmCaches() {
        getSubscriptionMap();
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    @Override
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.NOTIFICATION_SUBSCRIPTION)) {
            warmCaches();
        }
    }

    /** --- Internal Methods --- */

    @SuppressWarnings("unchecked")
    private HashMap<Integer, NotificationSubscription> getSubscriptionMap() {
        Element element = subCache.get(subCacheKey);
        if (element != null) {
            return (HashMap<Integer, NotificationSubscription>) element.getObjectValue();
        }
        Set<NotificationSubscription> subscriptionSet = subscriptionDao.getSubscriptions();
        HashMap<Integer, NotificationSubscription> subMap =
                new HashMap<>(Maps.uniqueIndex(subscriptionSet, NotificationSubscription::getId));
        subCache.put(new Element(subCacheKey, subMap));
        return subMap;
    }

    private void updateCachedSubscription(NotificationSubscription newSub) {
        getSubscriptionMap().put(newSub.getId(), newSub);
    }
}
