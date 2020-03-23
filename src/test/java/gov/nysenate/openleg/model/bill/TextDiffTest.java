package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TextDiffTest {

    private static final List<String> ADDED_CLASSES = Arrays.asList("ol-changed", "ol-added");
    private static final List<String> REMOVED_CLASSES = Arrays.asList("ol-changed", "ol-removed");
    private static final List<String> BOLD_CLASSES = Arrays.asList("ol-bold");
    private static final List<String> HEADER_CLASSES = Arrays.asList("ol-header");

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
    public void givenRemovedText_plainFormatIsUnchanged() {
        TextDiff diff = new TextDiff(TextDiffType.REMOVED, "[This text has been\nremoved.]");
        String actual = diff.getPlainFormatText();
        String expected = "[This text has been\nremoved.]";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_plainFormatIsUppercase() {
        TextDiff diff = new TextDiff(TextDiffType.ADDED, "This text has been\nadded.");
        String actual = diff.getPlainFormatText();
        String expected = "THIS TEXT HAS BEEN\nADDED.";
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
}
