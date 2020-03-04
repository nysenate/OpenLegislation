package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.model.bill.TextDiff;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BillTextDiffProcessorTest {

    private BillTextDiffProcessor textProcessor;

    @Before
    public void before() {
        textProcessor = new BillTextDiffProcessor();
    }

    @Test
    public void givenNullText_returnEmptyBillText() {
        String text = null;
        BillText actual = textProcessor.processBillText(text);

        BillText expected = new BillText(new ArrayList<>());
        assertEquals(expected, actual);
    }

    @Test
    public void givenEmptyText_returnEmptyBillText() {
        String text = "";
        BillText actual = textProcessor.processBillText(text);

        BillText expected = new BillText(new ArrayList<>());
        assertEquals(expected, actual);
    }

    // TODO XML should be validated and throw an exception if not valid.
    @Ignore
    @Test
    public void invalidXml() {
//        String text = "<B><U>THE NEW YORK STATE SENATE</B>";
        String text = "<B><U>THE NEW YORK STATE SENATE</B></U>";
        BillText actual = textProcessor.processBillText(text);
    }

    @Test
    public void recognizesUnchangedText() {
        String text = "THE NEW YORK STATE SENATE";
        BillText actual = textProcessor.processBillText(text);

        TextDiff diff = new TextDiff(0, text);
        BillText expected = new BillText(Arrays.asList(diff));
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesAddedText() {
        String text = "Commends the <B><U>Albany fire department</U></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends the "));
        diffs.add(new TextDiff(1, "Albany fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesRemovedText() {
        String text = "Commends the <B><S>Alb fire department</S></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends the "));
        diffs.add(new TextDiff(-1, "Alb fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesAddedAndRemovedTogether() {
        String text = "Commends the <B><S>Alb</S></B><B><U>Albany</U></B> fire department";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends the "));
        diffs.add(new TextDiff(-1, "Alb"));
        diffs.add(new TextDiff(1, "Albany"));
        diffs.add(new TextDiff(0, " fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void whitespaceIsPreserved() {
        String text = "     Commends      the <B><U>     Albany fire department</U></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "     Commends      the "));
        diffs.add(new TextDiff(1, "     Albany fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void newLinesPreserved() {
        String text = "Commends \n" +
                "the <B><U>Albany fire \n" +
                "department</U></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends \nthe "));
        diffs.add(new TextDiff(1, "Albany fire \ndepartment"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void styleElementIsRemoved() {
        String text = "\n<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
                "        P.brk {page-break-before:always}--></STYLE>\n" +
                "            STATE OF NEW YORK";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "\n            STATE OF NEW YORK"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void stripsOtherXmlElements() {
        String text = "<BASEFONT SIZE=3>\n" +
                "<PRE WIDTH=\"136\">\n" +
                "<FONT SIZE=5><B>STATE OF NEW YORK</B></FONT>\n" +
                "Commends the city of <B>Albany</B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "\n\nSTATE OF NEW YORK\nCommends the city of Albany"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }
}
