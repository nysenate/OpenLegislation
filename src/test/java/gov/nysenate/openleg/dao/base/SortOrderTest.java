package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static gov.nysenate.openleg.dao.base.SortOrder.*;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SortOrderTest {
    @Test
    public void getOppositeTest() {
        assertEquals(3, values().length);
        assertEquals(ASC, getOpposite(DESC));
        assertEquals(DESC, getOpposite(ASC));
        assertEquals(NONE, getOpposite(NONE));
    }
}
