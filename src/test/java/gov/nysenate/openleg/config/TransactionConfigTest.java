package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Category(SillyTest.class)
@Transactional
public class TransactionConfigTest extends BaseTests
{
    private static final
    Logger logger = LoggerFactory.getLogger(TransactionConfigTest.class);

    @Autowired
    private JdbcTemplate jdbc;

    @Transactional(rollbackFor = RuntimeException.class)
    public void testTransaction(int c) {
        jdbc.update("insert into lbdc.bill_daybreak values(1, 'STEST', " + c + ")");
        logger.info("WROTE TO DB");
        throw new RuntimeException("ROLLBACK!");
    }

    public void moose(int c) {
        logger.info("THROWING EXCEPTION");

    }

    @Test
    @Transactional
    public void testTransactionMethod() throws Exception {
        testTransaction(3212);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Rollback
    private void hah(int i) {
        testTransaction(i);
        moose(i);
    }
}
