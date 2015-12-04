package gov.nysenate.openleg.dao.bill.reference.daybreak;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBill;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SqlFsDaybreakDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsDaybreakDaoTests.class);

    @Autowired
    SqlFsDaybreakDao daybreakDao;

    @Test
    public void testGetDaybreakBill() throws Exception {
        logger.info("{}", daybreakDao.getCurrentReportDate().toString());
        List<DaybreakBill> daybreakBill = daybreakDao.getCurrentDaybreakBills();
        logger.info("{}", daybreakBill.size());
    }
}
