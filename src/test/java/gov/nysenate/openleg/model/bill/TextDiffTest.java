package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Ignore;
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

    @Test
    public void givenUnchangedText_plainFormatIsUnchanged() {
        TextDiff diff = new TextDiff(0, "Some unchanged basic\n text.");
        String actual = diff.getPlainText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_plainFormatIsUnchanged() {
        TextDiff diff = new TextDiff(-1, "[This text has been\nremoved.]");
        String actual = diff.getPlainText();
        String expected = "[This text has been\nremoved.]";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_plainFormatIsUppercase() {
        TextDiff diff = new TextDiff(1, "This text has been\nadded.");
        String actual = diff.getPlainText();
        String expected = "THIS TEXT HAS BEEN\nADDED.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenUnchangedText_htmlFormatUnchanged() {
        TextDiff diff = new TextDiff(0, "Some unchanged basic\n text.");
        String actual = diff.getHtmlText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenHeaderText_htmlFormatHasHeaderClass() {
        TextDiff diff = new TextDiff(0, "Some basic\n text.", HEADER_CLASSES);
        String actual = diff.getHtmlText();
        String expected = "<span class=\"ol-header\">Some basic\n text.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenBoldText_htmlFormatHasBoldClass() {
        TextDiff diff = new TextDiff(0, "Some basic\n text.", BOLD_CLASSES);
        String actual = diff.getHtmlText();
        String expected = "<span class=\"ol-bold\">Some basic\n text.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenAddedText_htmlFormatStyled() {
        TextDiff diff = new TextDiff(1, "This text has been\nadded.", ADDED_CLASSES);
        String actual = diff.getHtmlText();
        String expected = "<span class=\"ol-changed ol-added\">This text has been\nadded.</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_htmlFormatStyled() {
        TextDiff diff = new TextDiff(-1, "This text has been\nremoved.", REMOVED_CLASSES);
        String actual = diff.getHtmlText();
        String expected = "<span class=\"ol-changed ol-removed\">This text has been\nremoved.</span>";
        assertEquals(expected, actual);
    }
}
