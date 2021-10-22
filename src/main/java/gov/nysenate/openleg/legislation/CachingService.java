package gov.nysenate.openleg.legislation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

public abstract class CachingService<Key, Value> {
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);

    @Autowired
    protected CacheManager cacheManager;
    @Autowired
    protected EventBus eventBus;
    protected Cache<Key, Value> cache;

    @PostConstruct
    protected void init() {
        setupCaches();
        eventBus.register(this);
    }

    @PreDestroy
    protected void cleanUp() {
        evictCaches();
        for (ContentCache c : getCacheEnums())
            cacheManager.removeCache(c.name());
    }

    protected abstract List<ContentCache> getCacheEnums();

    protected abstract boolean isByteSizeOf();

    protected ResourceUnit getUnit() {
        // TODO: does this align with above? Use simple Interface?
        return EntryUnit.ENTRIES;
    }

    protected abstract int getNumUnits();

    // TODO: align types?
    protected ExpiryPolicy<? super Key, ? super Value> getExpiryPolicy() {
        return ExpiryPolicy.NO_EXPIRY;
    }

    /**
     * Performs cache creation and any pre-caching of data.
     */
    protected void setupCaches() {
        var cacheType = getCacheEnums().get(0);
        CacheConfiguration<Key, Value> config = (CacheConfiguration<Key, Value>) CacheConfigurationBuilder
                .newCacheConfigurationBuilder(cacheType.getKeyClass(), cacheType.getValueClass(),
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(getNumUnits(), getUnit()))
                .withExpiry(getExpiryPolicy()).withSizeOfMaxObjectGraph(isByteSizeOf() ? 5000 : 100000).build();
        cache = cacheManager.createCache(cacheType.name(), config);
    }

    /**
     * Returns all cache instances.
     * @return
     */
    public List<? extends Cache<?, ?>> getCaches() {
        return getCacheEnums().stream().map(c -> cacheManager.getCache(c.name(), c.getKeyClass(), c.getValueClass()))
                .toList();
    }

    /**
     * Evicts a single item from the cache based on the given content id
     */
    public void evictContent(Key key) {
        cache.remove(key);
    }

    /**
     * (Default Method)
     * Clears all the cache entries from all caches.
     */
     public void evictCaches() {
        if (getCaches() != null) {
            getCaches().forEach(c -> {
                // TODO: use Content Type
                logger.info("Clearing out a {} cache", c.toString());
                c.clear();
            });
        }
    }

    /**
     * If a CacheEvictEvent is sent out on the event bus, the caching service
     * should check to see if it has any affected caches and clear them.
     *
     * @param evictEvent CacheEvictEvent
     */
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (getCacheEnums().stream().anyMatch(evictEvent::affects))
            evictCaches();
    }

    /**
     * Intercept an evict Id event and evict the specified content
     * if the caching service has any of the affected caches
     * @param evictIdEvent CacheEvictIdEvent
     */
    @Subscribe
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Key> evictIdEvent) {
        if (getCacheEnums().stream().anyMatch(evictIdEvent::affects))
            evictContent(evictIdEvent.getContentId());
    }

    /**
     * Pre-fetch a subset of currently active data and store it in the cache.
     */
    public abstract void warmCaches();

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    @Subscribe
    public synchronized void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (getCacheEnums().stream().anyMatch(warmEvent::affects))
            warmCaches();
    }

    /**
     * The default side of configuration to use with caches sized by bytes on heap.
     * Sets the maximum limit for how many nodes are traversed when computing the heap
     * size of an object before raising a warning.
     *
     * Keep this low so we get warning messages when a cache's performance may be impacted
     * by the size of its object graph.
     *
     * @return SizeOfPolicyConfiguration
     */

    /**
     * An alternative size of configuration to be used only with caches sized by element count.
     * This uses a very high maxDepth to avoid warning messages as this heap size calculation is
     * only done when hitting the cache stats admin API.
     *
     * Some caches are sized by element count instead of bytes on heap because their object graphs are
     * large and calculating the heap size will effect its performance. These caches should be configured
     * with this policy so that erroneous warning messages are not received when we load the cache stats
     * ctrl or the cache stats UI.
     * @return
     */
}