package gov.nysenate.openleg.service.base.data;

import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT;

public interface CachingService
{
    /**
     * Returns the cache instance.
     */
    public Ehcache getCache();

    /**
     * Performs cache creation and any pre-caching of data.
     */
    public void setupCaches();

    /**
     * Clears all the cache entries.
     */
    public void evictCaches();

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
     * Default 'size of' configuration which sets the maximum limit for how many nodes are traversed
     * when computing the heap size of an object before bailing out to minimize performance impact.
     *
     * @return SizeOfPolicyConfiguration
     */
    public default SizeOfPolicyConfiguration defaultSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(50000).maxDepthExceededBehavior(ABORT);
    }
}