package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Category(UnitTest.class)
public class SessionTypeTest {
    @Test
    public void creationTest() {
        assertEquals("Regular Session", new SessionType("REGULAR SESSION").toString());
        assertEquals("Extraordinary Session II", new SessionType("Extra Ordinary Session II").toString());
        assertEquals("Extraordinary Session", new SessionType("Extraordinary Session").toString());
        assertEquals("Extraordinary Session II", new SessionType("SECOND EXTRAORDINARY SESSION").toString());
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Regular Session I"));
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Extraordinary Session A"));
        assertThrows(IllegalArgumentException.class, () -> new SessionType("Bad input"));
    }

    @Test
    public void compareTest() {
        var t1 = new SessionType("Regular Session");
        var t2 = new SessionType("Extraordinary Session");
        var t3 = new SessionType("Extraordinary Session I");
        var t4 = new SessionType("Extraordinary Session II");
        List<SessionType> expectedOrder = List.of(t1, t2, t3, t4);
        List<SessionType> actualOrder = expectedOrder.stream().sorted().toList();
        assertEquals(expectedOrder, actualOrder);
    }
}
