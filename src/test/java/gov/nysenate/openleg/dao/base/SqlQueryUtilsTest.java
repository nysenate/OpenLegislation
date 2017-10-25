package gov.nysenate.openleg.dao.base;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gov.nysenate.openleg.dao.base.SqlQueryUtils.getOrderByClause;
import static gov.nysenate.openleg.dao.base.SqlQueryUtils.getSqlWithSchema;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SqlQueryUtilsTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlQueryUtilsTest.class);

    @Test
    public void testGetSqlWithSchema() throws Exception {
        final String select = "SELECT 1 FROM ";
        final String table = "test";
        final String template = select + "${schema}." + table;
        final String schema = "master";
        final String orderByCol = "id";
        final SortOrder order = SortOrder.ASC;
        final OrderBy orderBy = new OrderBy(ImmutableMap.of(orderByCol, order));
        final int limit = 20;
        final int offset = 4;
        final LimitOffset limOff = new LimitOffset(limit, offset);

        final String expected = select + schema + "." + table + " " +
                "ORDER BY " + orderByCol + " " + order.name() + " " +
                "LIMIT " + limit + " OFFSET " + (offset - 1)
                ;


        final String actual = getSqlWithSchema(template, schema, orderBy, limOff);

        assertEquals("Templated schema should equal expected", expected, actual);
    }

    @Test
    public void testGetLimitOffsetClause() throws Exception {

    }

    @Test
    public void testGetOrderByClause() throws Exception {
        assertEquals("ORDER BY id DESC, name DESC", getOrderByClause(
            new OrderBy("id", SortOrder.DESC, "name", SortOrder.DESC)).trim());
        assertEquals("ORDER BY id DESC", getOrderByClause(
                new OrderBy("id", SortOrder.DESC, "name", SortOrder.NONE)).trim());
        assertEquals("", getOrderByClause(null).trim());
    }
}
