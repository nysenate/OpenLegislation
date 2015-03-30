package gov.nysenate.openleg.service.bill;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.CachedBillDataService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class CachedBillDataServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataServiceTests.class);

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
        billData.evictCaches();
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
