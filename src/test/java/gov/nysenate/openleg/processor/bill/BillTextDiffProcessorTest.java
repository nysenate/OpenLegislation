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

    // TODO These should all be defined somewhere...
    private static final List<String> ADDED_CLASSES = Arrays.asList("ol-changed", "ol-added");
    private static final List<String> REMOVED_CLASSES = Arrays.asList("ol-changed", "ol-removed");
    private static final List<String> BOLD_CLASSES = Arrays.asList("ol-bold");
    private static final List<String> HEADER_CLASSES = Arrays.asList("ol-header");

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
        diffs.add(new TextDiff(1, "Albany fire department", ADDED_CLASSES));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesRemovedText() {
        String text = "Commends the <B><S>Alb fire department</S></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends the "));
        diffs.add(new TextDiff(-1, "Alb fire department", REMOVED_CLASSES));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesAddedAndRemovedTogether() {
        String text = "Commends the <B><S>Alb</S></B><B><U>Albany</U></B> fire department";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends the "));
        diffs.add(new TextDiff(-1, "Alb", REMOVED_CLASSES));
        diffs.add(new TextDiff(1, "Albany", ADDED_CLASSES));
        diffs.add(new TextDiff(0, " fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesBoldedText() {
        String text = "<B>Commends</B> the Albany fire department";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "Commends", BOLD_CLASSES));
        diffs.add(new TextDiff(0, " the Albany fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void whitespaceIsPreserved() {
        String text = "     Commends      the <B><U>     Albany fire department</U></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "     Commends      the "));
        diffs.add(new TextDiff(1, "     Albany fire department", ADDED_CLASSES));
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
        diffs.add(new TextDiff(1, "Albany fire \ndepartment", ADDED_CLASSES));
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
                "STATE OF NEW YORK\n" +
                "Commends the city of Albany";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "\n\nSTATE OF NEW YORK\nCommends the city of Albany"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void fullTest() {
        String text = "\n<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
                "        P.brk {page-break-before:always}--></STYLE>\n" +
                "<FONT SIZE=5><B>     STATE OF NEW YORK</B></FONT>\n" +
                "\n" +
                "<B>Commends</B> the city of <B><S>albany</S></B><B><U>Albany</U></B> New York\n" +
                "on <B><S>there</S></B><B><U>their</U></B> work.";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(0, "\n"));
        diffs.add(new TextDiff(0, "     STATE OF NEW YORK", HEADER_CLASSES));
        diffs.add(new TextDiff(0, "\n\n"));
        diffs.add(new TextDiff(0, "Commends", BOLD_CLASSES));
        diffs.add(new TextDiff(0, " the city of "));
        diffs.add(new TextDiff(-1, "albany", REMOVED_CLASSES));
        diffs.add(new TextDiff(1, "Albany", ADDED_CLASSES));
        diffs.add(new TextDiff(0, " New York\non "));
        diffs.add(new TextDiff(-1, "there", REMOVED_CLASSES));
        diffs.add(new TextDiff(1, "their", ADDED_CLASSES));
        diffs.add(new TextDiff(0, " work."));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }
}
