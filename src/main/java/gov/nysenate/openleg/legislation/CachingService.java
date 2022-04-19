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
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;
import org.elasticsearch.core.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Service
public abstract class CachingService<Key, Value> {
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);
    private static final StatisticsService statisticsService = new DefaultStatisticsService();
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);
    // TODO: add this
    private final static EnumMap<CacheType, Tuple<Class<?>, Class<?>>> classMap = new EnumMap<>(CacheType.class);

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
        var classes = ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments();
        var keyClass = (Class<Key>) classes[0];
        var valueClass = (Class<Value>) classes[1];
        var type = cacheType();
        var currCache = cacheManager.getCache(type.name().toLowerCase(), keyClass, valueClass);
        if (currCache != null) {
            this.cache = currCache;
            return;
        }
        var initialEntries = initialEntries();
        int numEntries = (int) (initialEntries.size() * 1.2);
        if (numEntries == 0) {
            String size = environment.getProperty(type.name().toLowerCase() + ".cache.size");
            numEntries = size == null ? 50 : Integer.parseInt(size);
        }
        var resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(numEntries, EntryUnit.ENTRIES);
        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, resourcePoolsBuilder)
                .withSizeOfMaxObjectGraph(100000).withExpiry(ExpiryPolicy.NO_EXPIRY)
                .withEvictionAdvisor(evictionAdvisor());
        this.cache = cacheManager.createCache(type.name(), config);
        eventBus.register(this);
        handleCacheWarmEvent(new CacheWarmEvent(Set.of(type)));
    }

    @PreDestroy
    protected void cleanUp() {
        evictCache();
        cacheManager.removeCache(cacheType().name());
    }

    protected Value getCacheValue(Key key) {
        return cache == null ? null : cache.get(key);
    }

    protected void putCacheEntry(Key key, Value value) {
        if (cache != null)
            cache.put(key, value);
    }

    public Map<Key, Value> initialEntries() {
        return Map.of();
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
        if (evictEvent.affects(cacheType())) {
            evictCache();
        }
    }

    /**
     * Intercept an evict Id event and evict the specified content
     * if the caching service has any of the affected caches
     * @param evictIdEvent CacheEvictIdEvent
     */
    @Subscribe
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<Key> evictIdEvent) {
        if (evictIdEvent.affects(cacheType())) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    @Subscribe
    public synchronized void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(cacheType())) {
            evictCache();
            for (var entry : initialEntries().entrySet()) {
                cache.put(entry.getKey(), entry.getValue());
            }
        }
    }
}