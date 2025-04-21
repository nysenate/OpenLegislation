package gov.nysenate.openleg.common.dao;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.openleg.common.dao.SqlQueryUtils.getOrderByClause;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SqlQueryUtilsTest {
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
