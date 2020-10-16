package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Category(IntegrationTest.class)
public class TransactionConfigIT extends BaseTests
{
    private static final String testTableName = "rollback_test_" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    @Autowired private JdbcTemplate jdbc;

    @Test
    public void transactionTest1() {
        createTestTable();
    }

    @Test
    public void transactionTest2() {
        createTestTable();
    }

    @Test(expected = BadSqlGrammarException.class)
    public void transactionTest3() {
        createTestTable();
        createTestTable();
    }

    @Test
    public void transactionalOverrideTest() {
        createTestTable();
    }

    private void createTestTable() {
        jdbc.update("CREATE TABLE " + testTableName + "(id INT PRIMARY KEY)");
    }
}
