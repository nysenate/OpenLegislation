package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.processors.transcripts.session.Stenographer.*;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class StenographerTest {
    @Test
    public void dateTest() {
        LocalDateTime curr = LocalDate.of(1993, 1, 1).atStartOfDay();
        dateTestHelper(curr, WILLIMAN);
        dateTestHelper(curr.plusYears(2), WILLIMAN);
        curr = curr.withYear(1999);
        dateTestHelper(curr, CANDYCO2);
        dateTestHelper(curr.withYear(2002), CANDYCO2);
        curr = curr.withYear(2004);
        dateTestHelper(curr, NONE);
        dateTestHelper(curr.withMonth(6), NONE);
        curr = curr.withYear(2005);
        dateTestHelper(curr, CANDYCO1);
        dateTestHelper(curr.withYear(2010), CANDYCO1);
        curr = LocalDate.of(2011, 5, 16).atStartOfDay();
        dateTestHelper(curr, KIRKLAND);
        dateTestHelper(curr.withYear(2016), KIRKLAND);
        // New Stenographers may need to be added in the future.
        dateTestHelper(LocalDateTime.now(), KIRKLAND);
        try {
            getStenographer(LocalDate.of(1992, 1, 1).atStartOfDay());
            fail();
        }
        catch (RuntimeException ignored) {}
    }

    private void dateTestHelper(LocalDateTime ldt, Stenographer expected) {
        assertEquals(expected.getName(), getStenographer(ldt));
    }
}
