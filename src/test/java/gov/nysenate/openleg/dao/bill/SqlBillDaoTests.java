package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SqlBillDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDaoTests.class);

    @Autowired
    BillDao billDao;

    @Test
    public void testGetBill() throws Exception {
        logger.info("{}", OutputUtils.toJson(billDao.getBill(new BaseBillId("S1051", 2013))));
    }

    @Test
    public void testGetBillIdsBySession() throws Exception {
        StopWatch sw = new StopWatch();
        List<BaseBillId> baseBillIds = billDao.getBillIds(SessionYear.current(), LimitOffset.FIFTY, SortOrder.ASC);

        sw.start();
        baseBillIds = billDao.getBillIds(SessionYear.current(), LimitOffset.THOUSAND, SortOrder.ASC);
        logger.info("{}", OutputUtils.toJson(baseBillIds.size()));
        sw.stop();
        logger.info("{}", sw.getTime());
    }

    @Test
    public void testCountAllBills() throws Exception {
        logger.info("{}", billDao.getBillCount(SessionYear.current()));
    }

    @Test
    public void testFastBill() throws Exception {
        Bill bill = billDao.getBill(new BillId("S5922", 2013));

        StopWatch sw = new StopWatch();
        sw.start();
            bill = billDao.getBill(new BillId("S5922", 2013));
        sw.stop();
        logger.info("Time {} ms",sw.getTime());
//        logger.info("{}", OutputUtils.toJson(bill));
    }
}
