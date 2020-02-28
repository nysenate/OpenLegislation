package gov.nysenate.openleg;

import gov.nysenate.openleg.util.BillTextUtils;
import org.junit.Test;

public class testNonHTMLTextParse {

    @Test
    public void testNonHTMLTextParsing() {
        String sampleText = "This is the greatest bill. No one ever could have written a bill this good."
                + "\n" + "This bill is revolutionary";

        System.out.println(BillTextUtils.convertHtmlToPlainText(sampleText));
    }
}
