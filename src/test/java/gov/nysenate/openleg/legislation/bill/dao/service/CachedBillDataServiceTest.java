package gov.nysenate.openleg.legislation.bill.dao.service;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.CacheEvictEvent;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class CachedBillDataServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataServiceTest.class);

    @Autowired
    CachedBillDataService billData;

    @Autowired EventBus eventBus;

    @Test
    public void testGetBill() throws Exception {
        billData.getBill(new BaseBillId("S1234", 2013));
        StopWatch sw = new StopWatch();
        sw.start();
        Bill bill = billData.getBill(new BaseBillId("S2180", 2013));
        sw.stop();
        logger.info("{}", OutputUtils.toJson(bill));
        logger.info("{}", sw.getTime());
        sw.reset();
        sw.start();
        billData.getBill(new BaseBillId("S2180", 2013));
        sw.stop();
        logger.info("{}", sw.getTime());
    }

    @Test
    public void testGetBillInfo() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        sw.stop();
        sw.reset();
        logger.info("time {}", sw.getTime());
    }

    @Test
    public void evictContentTest() {
        StopWatch sw = new StopWatch();
        BaseBillId id = new BaseBillId("S1", 2015);
        billData.evictCache();
        sw.start();
        billData.getBill(id);
        sw.stop();
        logger.info("time {}", sw.getTime());
        sw.reset();
        sw.start();
        billData.getBill(id);
        sw.stop();
        logger.info("time {}", sw.getTime());
        sw.reset();
        billData.evictContent(id);
        sw.start();
        billData.getBill(id);
        sw.stop();
        logger.info("time {}", sw.getTime());
    }

    @Test
    public void testEvictEvent() throws Exception {
        eventBus.register(this);
        eventBus.post(new CacheEvictEvent(null));
    }
}
