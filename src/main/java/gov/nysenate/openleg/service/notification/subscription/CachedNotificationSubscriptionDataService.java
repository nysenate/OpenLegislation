package gov.nysenate.openleg.service.notification.subscription;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.notification.NotificationSubscriptionDao;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.notification.NotificationDigestSubscription;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CachedNotificationSubscriptionDataService implements NotificationSubscriptionDataService, CachingService
{

    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;

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

    /** --- NotificationSubscriptionDataService Implementation --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(String userName) {
        return getSubscriptions().stream()
                .filter(subscription -> StringUtils.equals(userName, subscription.getUserName()))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions(NotificationType type) {
        return getSubscriptions().stream()
                .filter(subscription -> subscription.getType().covers(type))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSubscription(NotificationSubscription subscription) {
        try {
            subscriptionDao.insertSubscription(subscription);
            evictCaches();
        } catch (DuplicateKeyException ignored){}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSubscription(NotificationSubscription subscription) {
        subscriptionDao.removeSubscription(subscription);
        evictCaches();
    }

    /** {@inheritDoc} */
    @Override
    public Set<NotificationDigestSubscription> getPendingDigests() {
        return subscriptionDao.getPendingDigests();
    }

    /** {@inheritDoc} */
    @Override
    public Set<NotificationDigestSubscription> getDigestSubsForUser(String username) {
        return subscriptionDao.getDigestSubsForUser(username);
    }

    /** {@inheritDoc} */
    @Override
    public void insertDigestSubscription(NotificationDigestSubscription subscription) {
        subscriptionDao.insertDigestSubscription(subscription);
    }

    /** {@inheritDoc} */
    @Override
    public void updateNextDigest(int digestSubscriptionId, LocalDateTime nextDigest) {
        subscriptionDao.updateNextDigest(digestSubscriptionId, nextDigest);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDigestSubscription(int digestSubscriptionId) {
        subscriptionDao.removeDigestSubscription(digestSubscriptionId);
    }

    /** --- CachingService Implementations --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupCaches() {
        subCache = new Cache(new CacheConfiguration().name(ContentCache.NOTIFICATION_SUBSCRIPTION.name())
                .eternal(true)
                .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(subCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(subCache);
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
        getSubscriptions();
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

    private Set<NotificationSubscription> getSubscriptions() {
        Element element = subCache.get(subCacheKey);
        if (element != null) {
            return (Set<NotificationSubscription>) element.getObjectValue();
        }
        Set<NotificationSubscription> subscriptions = subscriptionDao.getSubscriptions();
        subCache.put(new Element(subCacheKey, subscriptions));
        return subscriptions;
    }
}
