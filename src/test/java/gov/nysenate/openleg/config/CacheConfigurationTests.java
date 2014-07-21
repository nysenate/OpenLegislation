package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.util.OutputHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

public class CacheConfigurationTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CacheConfigurationTests.class);

    @Autowired
    CacheTester cacheTester;

    public static class CacheTester
    {
        net.sf.ehcache.CacheManager cacheManager;

        public CacheTester(net.sf.ehcache.CacheManager cacheManager) {
            cacheManager.addCache("test");
        }

        @Cacheable(value = "test", key = "#root.methodName + #s")
        public String method(String s) {
            logger.info("invoking method");
            return "moose";
        }

        @Cacheable(value = "test", key = "#root.methodName + #s")
        public List<String> methodList(String s) {
            logger.info("invoking methodList");
            return Arrays.asList("Moose", "cow");
        }

        @CacheEvict(value = "test")
        public void clear(int a) {

        }

        private void getCacheStats() {
        /* get stats for all known caches */
            StringBuilder sb = new StringBuilder();
            for (String name : cacheManager.getCacheNames()) {
                Cache cache = cacheManager.getCache(name);
                StatisticsGateway stats = cache.getStatistics();

                logger.debug(OutputHelper.toJson(stats));
            }
        }
    }

    @Test
    public void testCacheTester_usesCacheProperly() throws Exception {
        logger.info(cacheTester.method("a"));
        logger.info(cacheTester.method("a"));
        logger.info("{}", cacheTester.methodList("a"));
        logger.info("{}", cacheTester.methodList("a"));
    }


}
