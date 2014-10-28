package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CachedBillDataServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(CachedBillDataServiceTests.class);

    @Autowired
    BillDataService billData;

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
        logger.info("time {}", sw.getTime());
    }
}
