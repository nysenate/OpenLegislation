package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.BillDataService;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class CacheConfigurationTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CacheConfigurationTests.class);

    @Autowired
    CacheTester cacheTester;

    @Autowired
    BillDataService billDataService;

    public static class CacheTester
    {
        net.sf.ehcache.CacheManager cacheManager;

        public CacheTester(net.sf.ehcache.CacheManager cacheManager) {
            cacheManager.addCache("test");
        }

        @Cacheable(value = "test", key = "#root.methodName + #s")
        public String method(String s) {
            logger.info("invoking method1");
            return "moose";
        }

        public String method2(String s) {
            logger.info("invoking method2");
            return method(s);
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
        List<BillId> ids = Arrays.asList(new BillId("S123", 2013), new BillId("S1234", 2013));
        List<Bill> bills = ids.stream().map(billDataService::getBill).collect(toList());
        bills.forEach(f -> logger.info("{}", f.getTitle()));
//        logger.info("{}", bills);

//        logger.info(cacheTester.method("a"));
//        logger.info(cacheTester.method("a"));
//        logger.info(cacheTester.method2("a"));
    }


}
