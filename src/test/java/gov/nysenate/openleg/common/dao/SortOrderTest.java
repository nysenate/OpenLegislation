package gov.nysenate.openleg.common.dao;

import org.junit.Test;

import static gov.nysenate.openleg.common.dao.SortOrder.*;
import static org.junit.Assert.assertEquals;

public class SortOrderTest {
    @Test
    public void getOppositeTest() {
        assertEquals(3, values().length);
        assertEquals(ASC, getOpposite(DESC));
        assertEquals(DESC, getOpposite(ASC));
        assertEquals(NONE, getOpposite(NONE));
    }
}
