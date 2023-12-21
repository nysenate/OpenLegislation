package gov.nysenate.openleg.legislation;

import com.google.common.eventbus.EventBus;
import org.ehcache.Cache;
import org.ehcache.config.EvictionAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

@Service
public abstract class CachingService<Key, Value> {
    private static final int FOR_ROUNDING = 20;
    // Note: these are better left as field injections, to simplify subclass constructors.
    @Autowired
    private Environment environment;
    @Autowired
    protected EventBus eventBus;
    protected Cache<Key, Value> cache;

    protected abstract CacheType cacheType();

    protected EvictionAdvisor<Key, Value> evictionAdvisor() {
        return null;
    }

    @PostConstruct
    private synchronized void init() {
        Map<Key, Value> initialEntries = initialEntries();
        int size = getCacheSize(initialEntries.size());
        this.cache = OpenLegCacheManager.createCache(this, size);
        initialEntries.forEach((k, v) -> cache.put(k, v));
    }

    /**
     * Result is based on the number of initial entries
     * (rounded up to the nearest multiple of FOR_ROUNDING), or based on
     * the value in app.properties for bill caches.
     * @return the size of the cache to be created.
     */
    private int getCacheSize(int initialEntriesSize) {
        switch (cacheType()) {
            case BILL, BILL_INFO:
                String propertyString = cacheType().name().toLowerCase() + ".cache.size";
                String sizeStr = environment.getProperty(propertyString);
                if (sizeStr == null) {
                    throw new IllegalArgumentException("Error! Size for the " + cacheType() +
                            " cache was not configured.");
                }
                return Integer.parseInt(sizeStr);
            default:
                // 10% extra room is added, and this will return at least 20.
                return (int) (Math.floor(initialEntriesSize * 1.1/FOR_ROUNDING) + 1) * FOR_ROUNDING;
        }
    }

    @PreDestroy
    private void cleanUp() {
        cache.clear();
        OpenLegCacheManager.removeCache(cacheType());
    }

    protected Map<Key, Value> initialEntries() {
        return Map.of();
    }

    // This method is only needed to prevent type errors during compilation.
    void clearCache(boolean warmCaches) {
        cache.clear();
        if (warmCaches) {
            cache.putAll(initialEntries());
        }
    }
}
