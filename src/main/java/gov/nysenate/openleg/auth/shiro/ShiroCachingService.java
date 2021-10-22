package gov.nysenate.openleg.auth.shiro;

import gov.nysenate.openleg.legislation.CacheWarmEvent;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.ContentCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.nysenate.openleg.legislation.ContentCache.SHIRO;

/**
 * Created by Chenguang He on 10/19/2016.
 */
@Component
public class ShiroCachingService extends CachingService<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger(ShiroCachingService.class);
    private static final CacheConfiguration<Object, Object> cacheConfig = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(2, MemoryUnit.MB)).withExpiry(ExpiryPolicy.NO_EXPIRY).build();

    private final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(SHIRO.name(), cacheConfig).build(true);
    private Cache<Object, Object> shiroCache;

    public ShiroCachingService() {
        super(cache);
    }


    @Override
    protected List<ContentCache> getCacheEnums() {
        return List.of(SHIRO);
    }

    @Override
    protected boolean isByteSizeOf() {
        return true;
    }

    @Override
    protected int getNumUnits() {
        return 2;
    }

    @Override
    protected ResourceUnit getUnit() {
        return MemoryUnit.MB;
    }

    @Override
    public void setupCaches() {
        try {
            shiroCache = cacheManager.createCache(SHIRO.name(), cacheConfig);
        }
        catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void evictContent(Object object) {
        logger.debug("Evicting {}", object);
        shiroCache.remove(object);
    }

    @Override
    public void warmCaches() {}

    @Override
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {}

    protected Cache<Object, Object> getEhcache() {
        return shiroCache;
    }
}
