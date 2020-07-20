package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ImmutableParamsTest
{
    private static final List<String> keys = Arrays.asList("moose", "cow", "chicken");
    private static final List<String> values = new ArrayList<>();
    static {
        for (String k : keys)
            values.add(k + "Val");
    }

    @Test
    public void testFrom() {
        ImmutableParams params = ImmutableParams.from(
                new MapSqlParameterSource().addValue(keys.get(0), values.get(0))
                                           .addValue(keys.get(1), values.get(1)));
        assertEquals(2, params.getValues().size());
        assertEquals(values.get(0), params.getValue(keys.get(0)));
        assertEquals(values.get(1), params.getValue(keys.get(1)));
    }


    @Test
    public void testAdd() {
        ImmutableParams base = ImmutableParams.from(
                new MapSqlParameterSource().addValue(keys.get(0), values.get(0))
                        .addValue(keys.get(1), values.get(1)));
        ImmutableParams addtl = base.add(new MapSqlParameterSource(keys.get(2), values.get(2)));
        assertEquals(3, addtl.getValues().size());
        for (int i = 0; i < addtl.getValues().size(); i++)
            assertEquals(values.get(i), addtl.getValue(keys.get(i)));

        ImmutableParams baseCopy = ImmutableParams.from(new MapSqlParameterSource()).add(base);
        for (String k : base.paramSource.getParameterNames())
            assertEquals(base.getValue(k), baseCopy.getValue(k));
        for (String k : baseCopy.paramSource.getParameterNames())
            assertTrue(base.hasValue(k));
    }
}
