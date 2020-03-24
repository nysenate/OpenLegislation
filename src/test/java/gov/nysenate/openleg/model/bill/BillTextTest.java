package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.TestUtils;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.processor.bill.BillTextDiffProcessor;
import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillTextTest {

    @Test
    public void comparePlainTextFormatsOnFullBillText() throws URISyntaxException, IOException {
        File f = TestUtils.openTestResource("sourcefile/2019-06-05-15.10.44.369395_BILLTEXT_A01133D.XML");
        String text = FileUtils.readFileToString(f, StandardCharsets.UTF_8);

        BillTextDiffProcessor diffProcessor = new BillTextDiffProcessor();
        BillText billText =  diffProcessor.processBillText(text);
        String actual = billText.getFullText(BillTextFormat.PLAIN);
//        String expected = BillTextUtils.formatHtmlExtractedBillText(BillTextUtils.convertHtmlToPlainText(text));
        String expected = BillTextUtils.convertHtmlToPlainText(text);
        assertEquals(expected, actual);
    }
}
