package gov.nysenate.openleg.common.dao;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class LimitOffsetTest {
    @Test
    public void testHasLimit() {
        LimitOffset lo = new LimitOffset(0, 0);
        assertFalse(lo.hasLimit());

        lo = new LimitOffset(-1, 0);
        assertFalse(lo.hasLimit());

        lo = LimitOffset.ALL;
        assertFalse(lo.hasLimit());

        lo = new LimitOffset(1, 0);
        assertTrue(lo.hasLimit());
    }

    @Test
    public void testHasOffset() {
        LimitOffset lo = new LimitOffset(0, 0);
        assertFalse(lo.hasOffset());

        lo = LimitOffset.ALL;
        assertFalse(lo.hasOffset());

        lo = new LimitOffset(0, -1);
        assertFalse(lo.hasOffset());

        lo = new LimitOffset(0, 100);
        assertTrue(lo.hasOffset());

        lo = new LimitOffset(12, 100);
        assertTrue(lo.hasOffset());
    }

    @Test
    public void testLimitList() {
        List<Integer> list = ImmutableList.of(1,2,3,4,5,6,7,8,9,10);
        assertEquals(list.subList(0,list.size()), LimitOffset.limitList(list, LimitOffset.ALL));
        assertEquals(list.subList(0,2), LimitOffset.limitList(list, new LimitOffset(2)));
        assertEquals(list.subList(0,2), LimitOffset.limitList(list, new LimitOffset(2, 1)));
        assertEquals(list.subList(2,7), LimitOffset.limitList(list, new LimitOffset(5, 3)));
        assertEquals(list.subList(2,10), LimitOffset.limitList(list, new LimitOffset(50, 3)));
        assertEquals(list, LimitOffset.limitList(list, null));
    }

    @Test
    public void testNext() {
        assertEquals(new LimitOffset(20, 21), new LimitOffset(20).next());
        assertEquals(new LimitOffset(0, Integer.MAX_VALUE), new LimitOffset(0, 10).next());
        assertEquals(new LimitOffset(0, Integer.MAX_VALUE), LimitOffset.ALL.next());

    }
}
