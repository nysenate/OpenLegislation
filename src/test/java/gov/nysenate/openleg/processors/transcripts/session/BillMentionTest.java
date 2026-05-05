package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.transcripts.session.BillMention;
import gov.nysenate.openleg.legislation.transcripts.session.Position;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillMentionTest {
    @Test
    public void tagStartTest() {
        var testMention = new BillMention(new BillId("S1A", 2009), new Position(2, 4));
        String expectedTagStart = """
                <a href="/2009/S1A" target="_blank" class="link" id="2009-S1A-p2-l4">""";
        assertEquals(expectedTagStart, testMention.getTagStart());
    }
}
