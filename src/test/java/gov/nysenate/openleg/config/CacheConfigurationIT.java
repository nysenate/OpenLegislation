package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.TestConfig;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class CacheConfigurationIT extends BaseTests {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    ApplicationContext context;

    public static class CacheTesterConfig implements CacheTester {
        public CacheTesterConfig(CacheManager cacheManager) {
            var resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder().heap(1000, EntryUnit.ENTRIES);
            var config = CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(String.class, String.class, resourcePools);
            cacheManager.createCache("test", config);
        }

        @Cacheable(value = "test", key = "#root.methodName + #s")
        public String methodReturnsArg(String s) {
            return s;
        }

        @Cacheable(value = "test", key = "#root.methodName")
        public String methodReturnsMoose() {
            return "moose";
        }

        @CacheEvict(value = "test", allEntries = true)
        public void clear() {}
    }

    @Test
    public void usesCacheProperly() {
        TestConfig b = context.getBean("testConfig", TestConfig.class);
        CacheTester tester = b.cacheTester();
        var cache = cacheManager.getCache("test", String.class, String.class);
        assertNull(cache.get("methodReturnsMoose"));
        assertEquals(tester.methodReturnsMoose(), cache.get("methodReturnsMoose"));

        String arg = "argument";
        assertNull(cache.get("methodReturnsArg" + arg));
        assertEquals(tester.methodReturnsArg(arg), cache.get("methodReturnsArg" + arg));

        assertNull(cache.get("methodReturnsArgNotCached" + arg));
        tester.methodReturnsArgNotCached(arg);
        assertNull(cache.get("methodReturnsArgNotCached" + arg));

        tester.clear();
        assertFalse(cache.iterator().hasNext());
    }
}
