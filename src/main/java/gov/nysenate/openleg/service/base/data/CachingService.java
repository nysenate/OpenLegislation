package gov.nysenate.openleg.service.base.data;

import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT;

public interface CachingService
{
    static final Logger logger = LoggerFactory.getLogger(CachingService.class);

    /**
     * Performs cache creation and any pre-caching of data.
     */
    public void setupCaches();

    /**
     * Returns all cache instances.
     */
    public List<Ehcache> getCaches();

    /**
     * (Default Method)
     * Clears all the cache entries from all caches.
     */
    public default void evictCaches() {
        if (getCaches() != null && !getCaches().isEmpty()) {
            getCaches().forEach(c -> {
                logger.info("Clearing out {} cache", c.getName());
                c.removeAll();
            });
        }
    }

    /**
     * If a CacheEvictEvent is sent out on the event bus, the caching service
     * should check to see if it has any affected caches and clear them.
     *
     * @param evictEvent CacheEvictEvent
     */
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent);

    /**
     * Pre-fetch a subset of currently active data and store it in the cache.
     */
    public void warmCaches();

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent);

    /**
     * (Default Method)
     * Default 'size of' configuration which sets the maximum limit for how many nodes are traversed
     * when computing the heap size of an object before bailing out to minimize performance impact.
     *
     * @return SizeOfPolicyConfiguration
     */
    public default SizeOfPolicyConfiguration defaultSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(50000).maxDepthExceededBehavior(ABORT);
    }
}