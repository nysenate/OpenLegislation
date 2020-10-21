package gov.nysenate.openleg.processors.bill.xml;

import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BillText;
import gov.nysenate.openleg.legislation.bill.TextDiff;
import gov.nysenate.openleg.legislation.bill.TextDiffType;
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

    @Test
    public void givenSectionSymbolAltCode_convertsIntoSectionSymbol() {
        String text = "&#167;";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "§"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesUnchangedText() {
        String text = "THE NEW YORK STATE SENATE";
        BillText actual = textProcessor.processBillText(text);

        TextDiff diff = new TextDiff(TextDiffType.UNCHANGED, text);
        BillText expected = new BillText(Arrays.asList(diff));
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesAddedText() {
        String text = "Commends the <B><U>Albany fire department</U></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "Commends the "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "Albany fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesRemovedText() {
        String text = "Commends the <B><S>Alb fire department</S></B>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "Commends the "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "Alb fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesAddedAndRemovedTogether() {
        String text = "Commends the <B><S>Alb</S></B><B><U>Albany</U></B> fire department";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "Commends the "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "Alb"));
        diffs.add(new TextDiff(TextDiffType.ADDED, "Albany"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesBoldedText() {
        String text = "<B>Commends</B> the Albany fire department";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.BOLD, "Commends"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " the Albany fire department"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesHeaderText() {
        String text = "<FONT SIZE=5><B>                STATE OF NEW YORK</B></FONT>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.HEADER, "                STATE OF NEW YORK"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void recognizesPageBreak() {
        String text = "page one text\n" +
                "<P CLASS=\"brk\">\n" +
                "page two text";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "page one text\n"));
        diffs.add(new TextDiff(TextDiffType.PAGE_BREAK, ""));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\npage two text"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void whitespaceIsPreserved() {
        String text = "<PRE>     Commends      the <B><U>     Albany fire department</U></B></PRE>";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "     Commends      the "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "     Albany fire department"));
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
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "Commends \nthe "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "Albany fire \ndepartment"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void styleAndBaseFontElementIsRemoved() {
        String text = "\n<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
                "        P.brk {page-break-before:always}--></STYLE>\n" +
                "<BASEFONT SIZE=3>\n" +
                "<PRE WIDTH=\"127\">\n" +
                "            STATE OF NEW YORK";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n            STATE OF NEW YORK"));
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
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\nSTATE OF NEW YORK\nCommends the city of Albany"));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void fullTest() {
        String text = "<STYLE><!--U  {color: Green}S  {color: RED} I  {color: DARKBLUE; background-color:yellow}\n" +
                "P.brk {page-break-before:always}--></STYLE>\n" +
                "<BASEFONT SIZE=3>\n" +
                "<PRE WIDTH=\"127\">\n" +
                "\n" +
                "<FONT SIZE=5><B>                STATE OF NEW YORK</B></FONT>\n" +
                "\n" +
                "<B>Commends</B> the city of <B><S>albany</S></B><B><U>Albany</U></B> New York\n" +
                "on <B><S>there</S></B><B><U>their</U></B> work.";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n\n"));
        diffs.add(new TextDiff(TextDiffType.HEADER, "                STATE OF NEW YORK"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n\n"));
        diffs.add(new TextDiff(TextDiffType.BOLD, "Commends"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " the city of "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "albany"));
        diffs.add(new TextDiff(TextDiffType.ADDED, "Albany"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " New York\non "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "there"));
        diffs.add(new TextDiff(TextDiffType.ADDED, "their"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " work."));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void onlyOneEmptyLineBetweenPages() {
        String text = "<PRE>\n" +
                "         EXPLANATION--Matter in <B><U>italics</U></B> (underscored) is new; matter in brackets\n" +
                "                              [<B><S> </S></B>] is old law to be omitted.\n" +
                "                                                                   LBD03867-09-9\n" +
                "</PRE><P CLASS=\"brk\"><PRE WIDTH=\"127\">\n" + // This should be a single empty line.
                "A. 1133--D                          2\n" +
                "\n" +
                "1  provision  in  any section contained within a Part, including the effec-";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n         EXPLANATION--Matter in "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "italics"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " (underscored) is new; matter in brackets\n                              ["));
        diffs.add(new TextDiff(TextDiffType.REMOVED, " "));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "] is old law to be omitted.\n" +
                "                                                                   LBD03867-09-9\n"));
        diffs.add(new TextDiff(TextDiffType.PAGE_BREAK, ""));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n" +
                "A. 1133--D                          2\n" +
                "\n" +
                "1  provision  in  any section contained within a Part, including the effec-"));

        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void formatsPageBreaksCorrectly() {
        String text =
                "<PRE>    54  license and record database established pursuant to  section  400.02  of\n" +
                "    55  this chapter.\n" +
                "</PRE><P CLASS=\"brk\"><PRE WIDTH=\"99\">\n" +
                "        S. 2143--A                          3\n" +
                " \n" +
                "     1    (3)  Any  gunsmith  who  fails  to  comply with the provisions of this\n" +
                "     2  section shall be guilty of a class C felony.";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "    54  license and record database established pursuant to  section  400.02  of\n" +
                "    55  this chapter.\n"));
        diffs.add(new TextDiff(TextDiffType.PAGE_BREAK, ""));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "\n" +
                "        S. 2143--A                          3\n" +
                " \n" +
                "     1    (3)  Any  gunsmith  who  fails  to  comply with the provisions of this\n" +
                "     2  section shall be guilty of a class C felony."));

        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Test
    public void doesNotEscapeHtmlEntities() {
        String text = "<B><U>THE & NEW < YORK > STATE \" SENATE</U></B>";
        BillText actual = textProcessor.processBillText(text);

        TextDiff diff = new TextDiff(TextDiffType.ADDED, "THE & NEW < YORK > STATE \" SENATE");
        BillText expected = new BillText(Arrays.asList(diff));
        assertEquals(expected, actual);
    }

    @Test
    public void correctlyFormatsChangesSpanningPageBreak() {
        String text =
                "<PRE>    46      New York. Upon request of the commissioner of the office  of  [<B><S>alco-</S></B>\n" +
                "</PRE><P CLASS=\"brk\"><PRE WIDTH=\"136\">\n" +
                "                                           449                        12654-09-0\n" +
                "</S></B>\n" +
                "                              <B><S>DEPARTMENT OF MENTAL HYGIENE</S></B>\n" +
                "</S></B>\n" +
                "                  <B><S>OFFICE OF [ALCOHOLISM AND SUBSTANCE ABUSE</S></B>] <B><U>ADDICTION</U></B>\n" +
                "                                  SERVICES <B><U>AND SUPPORTS</U></B>\n" +
                " \n" +
                "                      CAPITAL PROJECTS - REAPPROPRIATIONS   2020-21\n" +
                " \n" +
                "     1      holism  and  substance  abuse]  <B><U>addiction</U></B>  services <B><U>and supports</U></B> and";

        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "    46      New York. Upon request of the commissioner of the office  of  ["));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "alco-"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "\n"));
        diffs.add(new TextDiff(TextDiffType.PAGE_BREAK, ""));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "\n" +
                "                                           449                        12654-09-0\n" +
                "\n                              "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "DEPARTMENT OF MENTAL HYGIENE"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "\n" +
                "\n" +
                "                  "));
        diffs.add(new TextDiff(TextDiffType.REMOVED, "OFFICE OF [ALCOHOLISM AND SUBSTANCE ABUSE"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "] "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "ADDICTION"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "\n" +
                "                                  SERVICES "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "AND SUPPORTS"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED,
                "\n" +
                " \n" +
                "                      CAPITAL PROJECTS - REAPPROPRIATIONS   2020-21\n" +
                " \n" +
                "     1      holism  and  substance  abuse]  "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "addiction"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "  services "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "and supports"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " and"));

        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    /**
     * Nested added/removed element tests.
     *
     * Normally bill text contains brackets and removed elements around text which has been removed
     * from the previous version. i.e. [<B><S>text that was removed</S></B>]
     *
     * However there appears to be a bug in the xml bill text we receive causing the <B><S></S></B> elements to be
     * added even in situations where brackets are used in the bill text itself.
     *
     * This can cause issues with our parser because it makes no sense for these tags to be nested in each other.
     *
     * To keep our raw text accurate, we remove the <B><S></S></B> element in these instances.
     *
     * Examples of this:
     * - 2019-01-15-18.16.27.610484_BILLTEXT_S01533.XML page 1 line 2-4
     * - 2020-03-12-11.04.18.634283_BILLTEXT_S01527C.XML page 76 line 4-8
     * - 2019-04-03-14.56.51.698683_BILLTEXT_S04984.XML page 12 line 49
     *
     * // TODO figure out a solution for these edge cases.
     */

    @Ignore
    @Test
    public void givenRemovedElementInsideAddedElement_removedElementIgnored() {
        String text = "Foo <B><U>Record & Return by [<B><S></S></B>] Mail [<B><S></S></B>] Pickup to:</U></B> bar.";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "Foo "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "Record & Return by [] Mail [] Pickup to:"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " bar."));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void givenAddedElementInsideRemovedElement_removeElementIgnored() {
        String text = "the [<B><S>new york <B><U>state</U></B> senate</S></B>] in albany.";
        BillText actual = textProcessor.processBillText(text);

        List<TextDiff> diffs = new ArrayList<>();
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, "the [new york "));
        diffs.add(new TextDiff(TextDiffType.ADDED, "state"));
        diffs.add(new TextDiff(TextDiffType.UNCHANGED, " senate] in albany."));
        BillText expected = new BillText(diffs);
        assertEquals(expected, actual);
    }
}
