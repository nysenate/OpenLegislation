package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TextDiffTest {

    @Test
    public void givenUnchangedText_plainFormatDoesNotRequireModifications() {
        TextDiff diff = new TextDiff(0, "Some unchanged basic\n text.");
        String actual = diff.getPlainText();
        String expected = "Some unchanged basic\n text.";
        assertEquals(expected, actual);
    }

    @Test
    public void givenRemovedText_plainFormatPutIntoBrackets() {
        TextDiff diff = new TextDiff(-1, "This text has been\nremoved.");
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
}
