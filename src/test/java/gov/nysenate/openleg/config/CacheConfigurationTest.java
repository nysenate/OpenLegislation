package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.TestConfig;
import gov.nysenate.openleg.annotation.IntegrationTest;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class CacheConfigurationTest extends BaseTests {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    ApplicationContext context;

    public static class CacheTesterConfig implements CacheTester{
        public CacheTesterConfig(CacheManager cacheManager) {
            Cache testCache = new Cache(new CacheConfiguration()
                    .name("test")
                    .maxEntriesLocalHeap(10000));
            cacheManager.addCache(testCache);
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
        Cache cache = cacheManager.getCache("test");
        assertNull(cache.get("methodReturnsMoose"));
        assertEquals(tester.methodReturnsMoose(), cache.get("methodReturnsMoose").getObjectValue());

        String arg = "argument";
        assertNull(cache.get("methodReturnsArg" + arg));
        assertEquals(tester.methodReturnsArg(arg), cache.get("methodReturnsArg" + arg).getObjectValue());

        assertNull(cache.get("methodReturnsArgNotCached" + arg));
        tester.methodReturnsArgNotCached(arg);
        assertNull(cache.get("methodReturnsArgNotCached" + arg));

        tester.clear();
        assertTrue(cache.getKeys().isEmpty());
    }
}
