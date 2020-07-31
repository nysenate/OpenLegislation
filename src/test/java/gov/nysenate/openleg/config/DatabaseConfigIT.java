package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

@Category(IntegrationTest.class)
public class DatabaseConfigIT extends BaseTests
{
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    public void testPostgreSQLConnectionPool() {
        Assert.assertNotNull(jdbc);
        Integer one = jdbc.queryForObject("SELECT 1", new SingleColumnRowMapper<>());
        Assert.assertEquals(new Integer(1), one);
    }
}
