package gov.nysenate.openleg.legislation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.EvictionAdvisor;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

@Service
public abstract class CachingService<Key, Value> {
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);
    private static final StatisticsService statisticsService = new DefaultStatisticsService();
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);

    @Autowired
    private Environment environment;
    @Autowired
    protected EventBus eventBus;
    protected Cache<Key, Value> cache;

    protected abstract CacheType cacheType();

    @Nonnull
    public static CacheStatistics getStats(CacheType type) {
        return statisticsService.getCacheStatistics(type.name());
    }

    public static Configuration cacheManagerConfig() {
        return cacheManager.getRuntimeConfiguration();
    }

    protected EvictionAdvisor<Key, Value> evictionAdvisor() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void init() {
        var type = cacheType();
        var classes = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments();
        Class<Key> keyClass = (Class<Key>) classes[0];
        Class<Value> valueClass = (Class<Value>) classes[1];
        var currCache = cacheManager.getCache(type.name().toLowerCase(), keyClass, valueClass);
        if (currCache != null) {
            this.cache = currCache;
            return;
        }

        String propertyStr = type.name().toLowerCase() + ".cache." +
                (type.isElementSize() ? "element" : "heap") + ".size";
        int numUnits = Integer.parseInt(Objects.requireNonNull(environment.getProperty(propertyStr)));
        var resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(numUnits, type.isElementSize() ? EntryUnit.ENTRIES : MemoryUnit.MB);
        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, resourcePoolsBuilder)
                .withSizeOfMaxObjectGraph(type.isElementSize() ? 100000 : 5000)
                .withExpiry(ExpiryPolicy.NO_EXPIRY)
                .withEvictionAdvisor(evictionAdvisor());
        this.cache = cacheManager.createCache(type.name(), config);
        eventBus.register(this);
    }

    @PreDestroy
    protected void cleanUp() {
        evictCache();
        cacheManager.removeCache(cacheType().name());
    }

    /**
     * Evicts a single item from the cache based on the given content id
     */
    public void evictContent(Key key) {
        cache.remove(key);
    }

    /**
     * Clears all the cache entries from this cache.
     */
     public void evictCache() {
         logger.info("Clearing out the {} cache", cacheType().name());
         cache.clear();
    }

    /**
     * If a CacheEvictEvent is sent out on the event bus, the caching service
     * should check to see if it has any affected caches and clear them.
     *
     * @param evictEvent CacheEvictEvent
     */
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(cacheType()))
            evictCache();
    }

    /**
     * Intercept an evict Id event and evict the specified content
     * if the caching service has any of the affected caches
     * @param evictIdEvent CacheEvictIdEvent
     */
    @Subscribe
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Key> evictIdEvent) {
        if (evictIdEvent.affects(cacheType()))
            evictContent(evictIdEvent.getContentId());
    }

    /**
     * Pre-fetch a subset of currently active data and store it in the cache.
     */
    public void warmCaches() {}

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    @Subscribe
    public synchronized void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(cacheType()))
            warmCaches();
    }
}