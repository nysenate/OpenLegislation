package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class BillDaoTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(BillDaoTest.class);

    @Autowired
    private BillDao billDao;

    @Test
    public void testActiveSessionRange() throws Exception {
        logger.info("{}", billDao.activeSessionRange());
    }
}
