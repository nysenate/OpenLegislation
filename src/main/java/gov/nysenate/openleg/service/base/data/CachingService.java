package gov.nysenate.openleg.service.base.data;

import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.CONTINUE;

public interface CachingService<ContentId>
{
    Logger logger = LoggerFactory.getLogger(CachingService.class);

    /**
     * Performs cache creation and any pre-caching of data.
     */
    void setupCaches();

    /**
     * Returns all cache instances.
     */
    List<Ehcache> getCaches();

    /**
     * Evicts a single item from the cache based on the given content id
     */
    void evictContent(ContentId contentId);

    /**
     * (Default Method)
     * Clears all the cache entries from all caches.
     */
    default void evictCaches() {
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
    void handleCacheEvictEvent(CacheEvictEvent evictEvent);

    /**
     * Intercept an evict Id event and evict the specified content
     * if the caching service has any of the affected caches
     * @param evictIdEvent CacheEvictIdEvent
     */
    void handleCacheEvictIdEvent(CacheEvictIdEvent<ContentId> evictIdEvent);

    /**
     * Pre-fetch a subset of currently active data and store it in the cache.
     */
    void warmCaches();

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    void handleCacheWarmEvent(CacheWarmEvent warmEvent);

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
    default SizeOfPolicyConfiguration byteSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(5000).maxDepthExceededBehavior(CONTINUE);
    }

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
    public default SizeOfPolicyConfiguration elementSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(100000).maxDepthExceededBehavior(CONTINUE);
    }
}