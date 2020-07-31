package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TextDiffTest {

    /**
     * Plain text format tests
     */

    @Test
    public void givenUnchangedText_plainFormatIsUnchanged() {
        TextDiff diff = new TextDiff(TextDiffType.UNCHANGED, "Some unchanged basic\n text.");
        String actual = diff.getPlainFormatText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_plainFormatIsUppercase() {
        TextDiff diff = new TextDiff(TextDiffType.ADDED, "This text has been\nadded.");
        String actual = diff.getPlainFormatText();
        String expected = "THIS TEXT HAS BEEN\nADDED.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_plainFormatIsUnchanged() {
        TextDiff diff = new TextDiff(TextDiffType.REMOVED, "[This text has been\nremoved.]");
        String actual = diff.getPlainFormatText();
        String expected = "[This text has been\nremoved.]";
        assertEquals(expected, actual);
    }

    /**
     * Html text format tests
     */

    @Test
    public void givenUnchangedText_htmlFormatIsUnchanged() {
        TextDiff diff = new TextDiff(TextDiffType.UNCHANGED, "Some unchanged basic\n text.");
        String actual = diff.getHtmlFormatText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_htmlTextIsInAddedElement() {
        TextDiff diff = new TextDiff(TextDiffType.ADDED, "This text has been\nadded.");
        String actual = diff.getHtmlFormatText();
        String expected = "<b><u>This text has been\nadded.</u></b>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_htmlTextIsInRemovedElement(){
        TextDiff diff = new TextDiff(TextDiffType.REMOVED, "This text has been removed.");
        String actual = diff.getHtmlFormatText();
        String expected = "<b><s>This text has been removed.</s></b>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenHeaderText_htmlTextIsInHeaderElement() {
        TextDiff diff = new TextDiff(TextDiffType.HEADER, "Some title");
        String actual = diff.getHtmlFormatText();
        String expected = "<font size=5><b>Some title</b></font>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenBoldText_htmlTextIsInBoldElement() {
        TextDiff diff = new TextDiff(TextDiffType.BOLD, "bold text");
        String actual = diff.getHtmlFormatText();
        String expected = "<b>bold text</b>";
        assertEquals(expected, actual);
    }


    /**
     * Template text format tests
     */

    @Test
    public void givenUnchangedText_templateFormatUnchanged() {
        TextDiff diff = new TextDiff(TextDiffType.UNCHANGED, "Some unchanged basic\n text.");
        String actual = diff.getTemplateFormatText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_templateFormatStyled() {
        TextDiff diff = new TextDiff(TextDiffType.ADDED, "This text has been\nadded.");
        String actual = diff.getTemplateFormatText();
        String expected = "<span class=\"ol-changed ol-added\">This text has been\nadded.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_templateFormatStyled() {
        TextDiff diff = new TextDiff(TextDiffType.REMOVED, "This text has been\nremoved.");
        String actual = diff.getTemplateFormatText();
        String expected = "<span class=\"ol-changed ol-removed\">This text has been\nremoved.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenHeaderText_templateFormatHasHeaderClass() {
        TextDiff diff = new TextDiff(TextDiffType.HEADER, "Some basic\n text.");
        String actual = diff.getTemplateFormatText();
        String expected = "<span class=\"ol-header\">Some basic\n text.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenBoldText_templateFormatHasBoldClass() {
        TextDiff diff = new TextDiff(TextDiffType.BOLD, "Some basic\n text.");
        String actual = diff.getTemplateFormatText();
        String expected = "<span class=\"ol-bold\">Some basic\n text.</span>";
        assertEquals(expected, actual);
    }
}
