package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.service.scraping.bill.LrsToSobiBillText;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LrsToSobiBillTextTest {

    private LrsToSobiBillText lrsToSobiBillText = new LrsToSobiBillText();

    @Test
    public void resolutionReplacesFirstLines() {
        // TODO: Why does the spacing end up so strange (7 spaces before "Center for Dispute", extra spaces between "commemorating the 30th Anniversary")
        String expected = "\nLEGISLATIVE RESOLUTION commemorating  the  30th  Anniversary of ACCORD, A\n" +
                "       Center for Dispute Resolution, Inc.";
        String lrsText = "\r\n \r\n" +
                "Senate Resolution No. 4405\r\n" +
                " \r\n" +
                "BY: Senator LIBOUS\r\n" +
                " \r\n" +
                "        COMMEMORATING  the  30th  Anniversary of ACCORD, A\r\n" +
                "        Center for Dispute Resolution, Inc.";
        String actual = lrsToSobiBillText.resolutionText(lrsText, Chamber.SENATE);
        assertEquals(expected, actual);
    }
}
