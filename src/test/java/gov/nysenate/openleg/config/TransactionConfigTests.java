package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.dao.base.ImmutableParams;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@TransactionConfiguration(defaultRollback = false)
@Transactional
public class TransactionConfigTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionConfigTests.class);

    private final int session = 2037;
    private final String printNo = "X5000";
    private final String activeVersion = "Q";

    private final String querySql =
            "SELECT COUNT(*)\n" +
                    "FROM master.bill\n" +
                    "WHERE bill_session_year = :session\n" +
                    "  AND bill_print_no = :printNo\n" +
                    "  AND active_version = :activeVersion\n";

    private final String insertSql =
            "INSERT INTO master.bill (bill_session_year, bill_print_no, active_version)\n" +
                    "                 VALUES (:session,          :printNo,      :activeVersion)\n";

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

    @Test(expected = UncategorizedSQLException.class)
    public void duplicateKeyTest() {
        logger.info("first insert...");
        insertBill();
        logger.info("second insert...");
        insertBill();
    }

    private void insertBill() {
        final ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("session", session)
                .addValue("printNo", printNo)
                .addValue("activeVersion", activeVersion)
        );
        try {
            jdbcNamed.update(insertSql, params);

        } catch (DuplicateKeyException ex) {
            logger.error("Duplicate KEY!!!");
        }
        final int postCount = jdbcNamed.queryForObject(querySql, params, Integer.class);
        logger.info("bills found: {}", postCount);
        Assert.assertEquals(
                "Bill " + printNo + activeVersion + "-" + session + " should have been inserted into the database",
                1, postCount);
    }

    private void rollbackTestMethod() {

        final ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("session", session)
                .addValue("printNo", printNo)
                .addValue("activeVersion", activeVersion)
        );

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
