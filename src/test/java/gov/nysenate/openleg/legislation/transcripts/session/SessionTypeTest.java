package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class SessionTypeTest {
    private static final SessionType regSess = new SessionType("Regular Session"),
            regSessCaps = new SessionType("REGULAR SESSION"),
            exSess = new SessionType("Extraordinary Session");

    @Test
    public void creationTest() {
        assertEquals("Regular Session", regSessCaps.toString());
        assertEquals("Extraordinary Session II", new SessionType("Extra Ordinary Session II").toString());
        assertEquals("Extraordinary Session", exSess.toString());
        assertEquals("Extraordinary Session II", new SessionType("SECOND EXTRAORDINARY SESSION").toString());
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Regular Session I"));
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Extraordinary Session A"));
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Bad input"));
    }

    @Test
    public void compareTest() {
        var t1 = new SessionType("Extraordinary Session I");
        var t2 = new SessionType("Extraordinary Session II");
        List<SessionType> expectedOrder = List.of(regSess, exSess, t1, t2);
        List<SessionType> actualOrder = expectedOrder.stream().sorted().toList();
        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    public void equalsTest() {
        TestUtils.basicEqualsTest(regSess, regSessCaps, exSess);
    }
}
