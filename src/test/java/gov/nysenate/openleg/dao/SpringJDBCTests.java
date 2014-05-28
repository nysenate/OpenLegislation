package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.util.Application;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpringJDBCTests extends BaseTests
{
    private static Logger logger = Logger.getLogger(SpringJDBCTests.class);

    @Test
    public void testQuery() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(Application.getDB().getDataSource());
        List<Integer> res = jdbcTemplate.query("SELECT 1", new SingleColumnRowMapper<Integer>());
        assertEquals(1, res.size());
        assertEquals(new Integer(1), res.get(0));
    }
}
