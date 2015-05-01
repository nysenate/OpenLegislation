package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.util.OutputUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Arrays;
import java.util.List;

public class CacheConfigurationTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CacheConfigurationTests.class);

    @Autowired
    CacheTester cacheTester;

    @Autowired
    BillDataService billDataService;

    @Autowired
    CacheManager cacheManager;

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

                logger.debug(OutputUtils.toJson(stats));
            }
        }
    }

    @Test
    public void testCacheTester_usesCacheProperly() throws Exception {
        //List<BillId> ids = Arrays.asList(new BillId("S123", 2013), new BillId("S1234", 2013));
        //List<Bill> bills = ids.stream().map(billDataService::getBill).collect(toList());
        //bills.forEach(f -> logger.info("{}", f.getTitle()));
//        logger.info("{}", bills);

//        logger.info(cacheTester.method("a"));
//        logger.info(cacheTester.method("a"));
//        logger.info(cacheTester.method2("a"));
    }

    @Test
    public void testCopyOnReadWrite() throws Exception {
//        cacheManager.addCache(new Cache(new CacheConfiguration().name("testCache").copyOnWrite(true)));
//        billDataService.getBillIds(SessionYear.of(2011), LimitOffset.ALL).parallelStream().forEach(b -> {
//            billDataService.getBill(b);
//            logger.info("{}", b);
//        });
//        billDataService.getBillIds(SessionYear.of(2013), LimitOffset.ALL).parallelStream().forEach(b -> {
//            billDataService.getBill(b);
//            logger.info("{}", b);
//        });
//        Bill S1234 = billDataService.getBill(new BaseBillId("S1234", 2013));
//        cacheManager.getCache("testCache").put(new Element(new BaseBillId("S1234", 2013), S1234));
//        S1234.getActiveVersion().setFullText("MUWAHHAHA");
//        assertEquals(S1234.getFullText(),
//                ((Bill) cacheManager.getCache("testCache").get(new BaseBillId("S1234", 2013)).getObjectValue()).getFullText());
//        logger.info("Bill Cache {}", cacheManager.getCache("bills").getStatistics().getSize());
//        logger.info("Pool Size {}", cacheManager.getOnHeapPool().getSize());
//        logger.info("Pool Max Size {}", cacheManager.getOnHeapPool().getMaxSize());
//        List<BaseBillId> billIds = (List<BaseBillId>) cacheManager.getCache("bills").getKeys();
//        logger.info("2011 count {}", billIds    .stream().filter(b -> b.getSession().getYear() == 2011).count());
//        logger.info("2013 count {}", billIds.stream().filter(b -> b.getSession().getYear() == 2013).count());

    }
}
