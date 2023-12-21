package gov.nysenate.openleg.legislation;

import gov.nysenate.openleg.BaseTests;
import org.ehcache.Cache;
import org.ehcache.core.statistics.CacheStatistics;
import org.junit.Before;

public abstract class AbstractCacheTest<Key, Value> extends BaseTests {
    private CachingService<Key, Value> cachingService;
    protected Cache<Key, Value> cache;
    protected CacheStatistics stats;

    protected abstract CachingService<Key, Value> getCachingService();

    @Before
    public void setCachingService() {
        this.cachingService = getCachingService();
        this.cache = cachingService.cache;
        this.stats = OpenLegCacheManager.getCacheStats(cachingService.cacheType());
    }
}
