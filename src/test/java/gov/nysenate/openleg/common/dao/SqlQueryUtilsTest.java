package gov.nysenate.openleg.common.dao;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.openleg.common.dao.SqlQueryUtils.getOrderByClause;
import static gov.nysenate.openleg.common.dao.SqlQueryUtils.getSqlWithSchema;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SqlQueryUtilsTest {

    @Test
    public void testGetSqlWithSchema() {
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
    public void testGetLimitOffsetClause() {

    }

    @Test
    public void testGetOrderByClause() {
        assertEquals("ORDER BY id DESC, name DESC", getOrderByClause(
            new OrderBy("id", SortOrder.DESC, "name", SortOrder.DESC)).trim());
        assertEquals("ORDER BY id DESC", getOrderByClause(
                new OrderBy("id", SortOrder.DESC, "name", SortOrder.NONE)).trim());
        assertEquals("", getOrderByClause(null).trim());
    }
}
