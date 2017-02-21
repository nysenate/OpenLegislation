package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.ImmutableParams;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@TransactionConfiguration(defaultRollback = false)
@Transactional
public class TransactionConfigTests extends BaseTests
{
    private static final
    Logger logger = LoggerFactory.getLogger(TransactionConfigTests.class);

    @Autowired private JdbcTemplate jdbc;

    @Autowired private NamedParameterJdbcTemplate jdbcNamed;

    @Transactional(rollbackFor = RuntimeException.class)
    public void testTransaction(int c) {
        jdbc.update("insert into lbdc.bill_daybreak values(1, 'STEST', " + c + ")");
        logger.info("WROTE TO DB");
        throw new RuntimeException("ROLLBACK!");
    }

    public void moose(int c) {
        logger.info("THROWING EXCEPTION");

    }

    @Test(expected = RuntimeException.class)
    @Transactional
    public void testTransactionMethod() throws Exception {
        testTransaction(3212);
    }

    @Test
    @Rollback
    public void rollbackTest() {
        rollbackTestMethod();
    }

    @Test
    @Rollback
    public void anotherRollbackTest() {
        rollbackTestMethod();
    }

    private void rollbackTestMethod() {
        final int session = 2037;
        final String printNo = "X5000";
        final String activeVersion = "Q";

        final ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("session", session)
                .addValue("printNo", printNo)
                .addValue("activeVersion", activeVersion)
        );

        final String querySql =
                "SELECT COUNT(*)\n" +
                "FROM master.bill\n" +
                "WHERE bill_session_year = :session\n" +
                "  AND bill_print_no = :printNo\n" +
                "  AND active_version = :activeVersion\n";

        final String insertSql =
                "INSERT INTO master.bill (bill_session_year, bill_print_no, active_version)\n" +
                "                 VALUES (:session,          :printNo,      :activeVersion)\n";

        final int preCount = jdbcNamed.queryForObject(querySql, params, Integer.class);
        Assert.assertEquals(
                "Bill " + printNo + activeVersion + "-" + session + " should not exist in the database",
                0, preCount);

        jdbcNamed.update(insertSql, params);

        final int postCount = jdbcNamed.queryForObject(querySql, params, Integer.class);
        Assert.assertEquals(
                "Bill " + printNo + activeVersion + "-" + session + " should have been inserted into the database",
                1, postCount);
    }

}
