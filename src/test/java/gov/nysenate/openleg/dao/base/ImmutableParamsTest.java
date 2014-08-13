package gov.nysenate.openleg.dao.base;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import static org.junit.Assert.assertEquals;

public class ImmutableParamsTest
{
    @Test
    public void testFrom() throws Exception {
        ImmutableParams params = ImmutableParams.from(
                new MapSqlParameterSource().addValue("moose", "mooseVal")
                                           .addValue("cow", "cowVal"));
        assertEquals(2, params.getValues().size());
        assertEquals("mooseVal", params.getValue("moose"));
        assertEquals("cowVal", params.getValue("cow"));
    }


    @Test
    public void testAdd() throws Exception {
        ImmutableParams base = ImmutableParams.from(
                new MapSqlParameterSource().addValue("moose", "mooseVal")
                        .addValue("cow", "cowVal"));
        ImmutableParams addtl = base.add(new MapSqlParameterSource("chicken", "chickenVal"));
        assertEquals(3, addtl.getValues().size());
        assertEquals("mooseVal", addtl.getValue("moose"));
        assertEquals("cowVal", addtl.getValue("cow"));
        assertEquals("chickenVal", addtl.getValue("chicken"));
    }
}
