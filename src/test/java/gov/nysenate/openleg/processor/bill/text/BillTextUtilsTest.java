package gov.nysenate.openleg.processor.bill.text;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.util.BillTextUtils;
import gov.nysenate.openleg.util.XmlHelper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class BillTextUtilsTest {
    final String bill = "\n<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
            "        P.brk {page-break-before:always}--></STYLE>\n" +
            "<BASEFONT SIZE=3>\n" +
            "<PRE WIDTH=\"136\">\n" +
            "\n" +
            "<FONT SIZE=5><B>                STATE OF NEW YORK</B></FONT>\n" +
            "                ________________________________________________________________________\n" +
            "        155\n" +
            "        2019-2020 Regular Sessions\n" +
            "\n" +
            "<FONT SIZE=5><B>                    IN SENATE</B></FONT>\n" +
            "                                       <B><U>(Prefiled)</U></B>\n" +
            "!!text has been abridged for test purposes!!\n" +
            "                EXPLANATION--Matter in <B><U>italics</U></B> (underscored) is new; matter in brackets\n" +
            "                [<B><S>removed text</S></B>] is old law to be omitted.\n" +
            "        LBD04958-01-9\n";


    @Test
    public void senateBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         100--A\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                    IN SENATE\n" +
                " \n" +
                "                                       (Prefiled)\n" +
                " \n" +
                "                                     January 4, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  Sen. HOYLMAN -- read twice and ordered printed, and when\n" +
                "          printed to be committed to the Committee  on  Consumer  Protection  --\n" +
                "          recommitted to the Committee on Consumer Protection in accordance with\n" +
                "          Senate  Rule  6, sec. 8 -- committee discharged, bill amended, ordered";
        String expectedResult = "\n" +
                "                           S T A T E   O F   N E W   Y O R K\n" +
                "       ________________________________________________________________________\n" +
                "\n" +
                "                                        100--A\n" +
                "\n" +
                "                              2017-2018 Regular Sessions\n" +
                "\n" +
                "                                   I N  S E N A T E\n" +
                "\n" +
                "                                      (PREFILED)\n" +
                "\n" +
                "                                    January 4, 2017\n" +
                "                                      ___________\n" +
                "\n" +
                "       Introduced  by  Sen. HOYLMAN -- read twice and ordered printed, and when\n" +
                "         printed to be committed to the Committee  on  Consumer  Protection  --\n" +
                "         recommitted to the Committee on Consumer Protection in accordance with\n" +
                "         Senate  Rule  6, sec. 8 -- committee discharged, bill amended, ordered";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void assemblyBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "                                         1051--A\n" +
                "                                                                 Cal. No. 17\n" +
                " \n" +
                "                               2017-2018 Regular Sessions\n" +
                " \n" +
                "                   IN ASSEMBLY\n" +
                " \n" +
                "                                    January 10, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        Introduced  by  M.  of  A.  SIMON,  ARROYO,  BLAKE, BARRETT, BRAUNSTEIN,";
        String expectedResult = "\n" +
                "                           S T A T E   O F   N E W   Y O R K\n" +
                "       ________________________________________________________________________\n" +
                "\n" +
                "                                        1051--A\n" +
                "                                                                Cal. No. 17\n" +
                "\n" +
                "                              2017-2018 Regular Sessions\n" +
                "\n" +
                "                                 I N  A S S E M B L Y\n" +
                "\n" +
                "                                   January 10, 2017\n" +
                "                                      ___________\n" +
                "\n" +
                "       Introduced  by  M.  of  A.  SIMON,  ARROYO,  BLAKE, BARRETT, BRAUNSTEIN,";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void uniBillTextFormatTest() {
        String inputText = " \n" +
                "                STATE OF NEW YORK\n" +
                "        ________________________________________________________________________\n" +
                " \n" +
                "            S. 2005--C                                            A. 3005--C\n" +
                " \n" +
                "                SENATE - ASSEMBLY\n" +
                " \n" +
                "                                    January 23, 2017\n" +
                "                                       ___________\n" +
                " \n" +
                "        IN  SENATE -- A BUDGET BILL, submitted by the Governor pursuant to arti-";
        String expectedResult = "\n" +
                "                           S T A T E   O F   N E W   Y O R K\n" +
                "       ________________________________________________________________________\n" +
                "\n" +
                "           S. 2005--C                                            A. 3005--C\n" +
                "\n" +
                "                             S E N A T E - A S S E M B L Y\n" +
                "\n" +
                "                                   January 23, 2017\n" +
                "                                      ___________\n" +
                "\n" +
                "       IN  SENATE -- A BUDGET BILL, submitted by the Governor pursuant to arti-";
        assertEquals(expectedResult, BillTextUtils.formatHtmlExtractedBillText(inputText));
    }

    @Test
    public void resolutionReplacesFirstLines() {
        String expected = "\nLEGISLATIVE RESOLUTION commemorating  the  30th  Anniversary of ACCORD, A\n" +
                "        Center for Dispute Resolution, Inc.";
        String lrsText = "\n\n" +
                "Senate Resolution No. 4405\n" +
                " \n" +
                "BY: Senator LIBOUS\n" +
                " \n" +
                "        COMMEMORATING  the  30th  Anniversary of ACCORD, A\n" +
                "        Center for Dispute Resolution, Inc.";
        String actual = BillTextUtils.formatHtmlExtractedResoText(lrsText);
        assertEquals(expected, actual);
    }

    @Test
    public void resolutionParse() {
        String input = "\n<PRE WIDTH=\"200\">\n" +
                "\n" +
                "Senate Resolution No. 485\n" +
                "\n" +
                "<B>BY:</B> Senator BROOKS\n" +
                "\n" +
                "<B>CONGRATULATING</B>  Maureen A. Griffin-Damone upon the\n" +
                "occasion  of  being  crowned  Ms.  New  York  Senior\n" +
                "America on July 29, 2018\n" +
                "!!text has been abridged for test purposes!!\n" +
                "</pre>\n";
        String output = BillTextUtils.toHTML5(input);
        //System.out.println("Tagless: " + output);
        assertEquals(-1, output.indexOf("<PRE WIDTH"));
        assertNotEquals(output.indexOf("!!text has been abridged for test purposes!!"), -1);
        String outputWithTags = BillTextUtils.toHTML5WithTags(input);
        //System.out.println("With tags: " + outputWithTags);
        assertEquals(-1, outputWithTags.indexOf("<PRE WIDTH"));
        assertNotEquals(outputWithTags.indexOf("!!text has been abridged for test purposes!!"), -1);
    }

    @Test
    public void billParse() {
        String output = BillTextUtils.toHTML5(bill);
        //System.out.println("Tagless: " + output);
        assertEquals(-1, output.indexOf("<PRE WIDTH"));
        assertEquals(-1, output.indexOf("<FONT"));
        assertNotEquals(output.indexOf("!!text has been abridged for test purposes!!"), -1);
        String outputWithTags = BillTextUtils.toHTML5WithTags(bill);
        //System.out.println("With tags: " + outputWithTags);
        assertEquals(-1, outputWithTags.indexOf("<PRE WIDTH"));
        assertEquals(-1, outputWithTags.indexOf("<FONT"));
        assertNotEquals(outputWithTags.indexOf("!!text has been abridged for test purposes!!"), -1);
        assertNotEquals(outputWithTags.indexOf("ol-text-added"), -1);
        assertNotEquals(outputWithTags.indexOf("ol-text-removed"), -1);
    }

    @Test
    public void billParseChanges() {
        ArrayList<BillTextUtils.TextDiff> changes = BillTextUtils.toChanges(bill);
        //for (int i = 0; i < changes.size(); ++i) System.out.println(changes.get(i));
        assertEquals(7, changes.size());
        String plain = BillTextUtils.convertHtmlToPlainText(bill).trim();
        String alsoPlain = BillTextUtils.changesToFinalText(changes).trim();
        assertNotEquals(plain, alsoPlain);//they're not supposed to be the same
        assertEquals(-1, alsoPlain.indexOf("removed text"));
    }

    @Test
    public void billParseChangeJSON() {
        ArrayList<BillTextUtils.TextDiff> changes = BillTextUtils.toChanges(bill);
        String output = BillTextUtils.changesToJSONString(changes);
        System.out.println(output);
    }


    @Test
    public void textDiff() {
        BillTextUtils.TextDiff a = new BillTextUtils.TextDiff(0, "plain", "<B><U>plain</U></B>");
        assertEquals(0, a.type);
        assertEquals("plain", a.text);
        assertEquals("<B><U>plain</U></B>", a.html);
    }

    @Test
    public void exitSuccessOnBrokenTags() {
        ArrayList<BillTextUtils.TextDiff> changes = BillTextUtils.toChanges("<B><U>test</S></B>");
        String result = BillTextUtils.changesToFinalText(changes);
        //System.out.println(result);
        changes = BillTextUtils.toChanges("[<B><S>test</U></B>");
        result = BillTextUtils.changesToFinalText(changes);
        //System.out.println(result);
        changes = BillTextUtils.toChanges("</S></B>]test[<B><S>");
        result = BillTextUtils.changesToFinalText(changes);
        //System.out.println(result);
    }

    @Test
    public void compareWithPOC() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        String[] files = {"src/test/resources/processor/bill/text/2017-01-01-00.00.00.000000_BILLTEXT_S09999.XML",
                            "src/test/resources/processor/bill/text/2017-02-09-14.23.24.464033_BILLTEXT_A05457.XML"};
        for (int i = 0; i < files.length; ++i) {
            String contents = new String(Files.readAllBytes(Paths.get(files[i])));
            XmlHelper xmlHelper = new XmlHelper();
            Node a = xmlHelper.parse(contents);
            String raw = xmlHelper.getString("/billtext_html", a);
            //System.out.println(raw);
            String html5 = BillTextUtils.toHTML5(raw);
            ArrayList<BillTextUtils.TextDiff> changes = BillTextUtils.toChanges(raw);
            //System.out.println(html5);
            String html5Tags = BillTextUtils.toHTML5WithTags(raw);
            String finalText = BillTextUtils.changesToFinalText(changes);
            String diffJSON = BillTextUtils.changesToJSONString(changes);
            assertNotEquals(-1, raw.indexOf("FONT"));
            assertEquals(-1, html5.indexOf("FONT"));
            assertEquals(-1, html5Tags.indexOf("FONT"));
            assertNotEquals(-1, html5Tags.indexOf("ol-text"));
            assertNotEquals(-1, raw.indexOf("STATE OF NEW YORK"));
            assertNotEquals(-1, html5.indexOf("STATE OF NEW YORK"));
            assertNotEquals(-1, html5Tags.indexOf("STATE OF NEW YORK"));
            assertTrue(isValidJSON(diffJSON));
            /*
            if (i == 0) {
                Files.write(Paths.get("/home/aa/btp/j_output/html5.html"), html5.getBytes());
                Files.write(Paths.get("/home/aa/btp/j_output/html5_tags.html"), html5Tags.getBytes());
                Files.write(Paths.get("/home/aa/btp/j_output/final.txt"), finalText.getBytes());
                Files.write(Paths.get("/home/aa/btp/j_output/diff.json"), diffJSON.getBytes());
            }*/
        }

    }

    /**
     * requires BillTextUtils.jsoupParsePreserveNewline() to be public access
     */
    /*
    @Test
    public void plainTextOutput() {
        assertEquals("", BillTextUtils.jsoupParsePreserveNewline(""));
        assertEquals("", BillTextUtils.jsoupParsePreserveNewline("<B></B>"));
        assertEquals("\n", BillTextUtils.jsoupParsePreserveNewline("\n<B></B>"));
        assertEquals("\n", BillTextUtils.jsoupParsePreserveNewline("<B></B>\n"));
        assertEquals("\n\n", BillTextUtils.jsoupParsePreserveNewline("\n\n<B></B>"));
        assertEquals("\n \n", BillTextUtils.jsoupParsePreserveNewline("\n \n<B></B>"));
        assertEquals(" \n", BillTextUtils.jsoupParsePreserveNewline(" \n"));
        assertEquals("\n \n", BillTextUtils.jsoupParsePreserveNewline("\n \n"));
        assertEquals("\nA\n", BillTextUtils.jsoupParsePreserveNewline("\nA\n"));
        assertEquals("abcd", BillTextUtils.jsoupParsePreserveNewline("abcd"));
        assertEquals("§", BillTextUtils.jsoupParsePreserveNewline("&#167;"));
    }*/

    /**
     * requires BillTextUtils.leadingWhitespace() to be public access
     */
    /*
    @Test
    public void leadingWhitespace() {
        assertEquals("", BillTextUtils.leadingWhitespace(""));
        assertEquals("", BillTextUtils.leadingWhitespace("<B></B>"));
        assertEquals("\n", BillTextUtils.leadingWhitespace("\n<B></B>"));
        assertEquals("", BillTextUtils.leadingWhitespace("<B></B>\n"));
        assertEquals("\n\n", BillTextUtils.leadingWhitespace("\n\n<B></B>"));
        assertEquals("\n \n", BillTextUtils.leadingWhitespace("\n \n<B></B>"));
        assertEquals(" \n", BillTextUtils.leadingWhitespace(" \n"));
        assertEquals("\n \n", BillTextUtils.leadingWhitespace("\n \n"));
        assertEquals("", BillTextUtils.leadingWhitespace("abcd"));
        assertEquals("", BillTextUtils.leadingWhitespace("&#167;"));
    }*/
    public boolean isValidJSON(final String json) {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory()
                    .createJsonParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return valid;
    }
}
