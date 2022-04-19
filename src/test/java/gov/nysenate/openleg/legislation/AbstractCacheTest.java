package gov.nysenate.openleg.legislation;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.Pair;
import org.ehcache.Cache;
import org.ehcache.core.statistics.CacheStatistics;
import org.junit.Assert;
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
        this.stats = CachingService.getStats(cachingService.cacheType());
    }

    public static void runTests(CachingService<?, ?> cachingService, Pair<?> sampleData) {
        CacheType type = cachingService.cacheType();
        cachingService.init();
        CacheStatistics stats;
        try {
            stats = CachingService.getStats(type);
        }
        catch (IllegalArgumentException ignored) {
            Assert.fail("Cache " + type + " does not have stats.");
        }
    }
}
