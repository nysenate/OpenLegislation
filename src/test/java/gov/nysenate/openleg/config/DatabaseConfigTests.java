package gov.nysenate.openleg.config;

import gov.nysenate.openleg.BaseTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

public class DatabaseConfigTests extends BaseTests
{
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    public void testPostgreSQLConnectionPool() throws Exception {
        Assert.assertNotNull(jdbc);
        Integer one = jdbc.queryForObject("SELECT 1", new SingleColumnRowMapper<Integer>());
        Assert.assertEquals(1, one.intValue());
    }
}
