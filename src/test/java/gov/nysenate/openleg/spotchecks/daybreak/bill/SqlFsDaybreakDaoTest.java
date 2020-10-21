package gov.nysenate.openleg.spotchecks.daybreak.bill;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Category(SillyTest.class)
public class SqlFsDaybreakDaoTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsDaybreakDaoTest.class);

    @Autowired
    SqlFsDaybreakDao daybreakDao;

    @Test
    public void testGetDaybreakBill() throws Exception {
        logger.info("{}", daybreakDao.getCurrentReportDate().toString());
        List<DaybreakBill> daybreakBill = daybreakDao.getCurrentDaybreakBills();
        logger.info("{}", daybreakBill.size());
    }
}
