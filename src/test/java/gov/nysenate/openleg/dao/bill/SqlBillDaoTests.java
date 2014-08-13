package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.bill.BaseBillId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlBillDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBillDaoTests.class);

    @Autowired
    BillDao billDao;

    @Test
    public void testGetBill() throws Exception {
        logger.info("{}", billDao.getBill(new BaseBillId("S1234", 2013)));
    }
}
