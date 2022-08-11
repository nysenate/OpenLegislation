package gov.nysenate.openleg.spotchecks.mismatch;

import gov.nysenate.openleg.config.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class HtmlDiffFormatterTest {

    private static String rawText = """
            Assembly Resolution No. 459
            \s
                    CONCURRENT    RESOLUTION    OF    THE    SENATE  AND  ASSEMBLY
                    relative to the adjournment
            """;

    @Test
    public void givenCRLFLineEndings_convertToLF() {
        // Java text blocks use LF at the end of each line. Here '\r' is added so each line ends with '\r\n'.
        String text = """
                Assembly Resolution No. 459\r
                \s\r
                        CONCURRENT    RESOLUTION    OF    THE    SENATE  AND  ASSEMBLY\r
                        relative to the adjournment\r
                """;
        String actual = HtmlDiffFormatter.normalizeLineEndings(text);
        String expected = """
                Assembly Resolution No. 459
                \s
                        CONCURRENT    RESOLUTION    OF    THE    SENATE  AND  ASSEMBLY
                        relative to the adjournment
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void givenWhitespaceOptionNONE_makeNoChanges() {
        String actual = HtmlDiffFormatter.applyWhitespaceOption(rawText, WhitespaceOption.NONE);
        assertEquals(rawText, actual);
    }

    @Test
    public void givenWhitespaceOptionNORMALIZE_WHITESPACE_trimAllWhitespace() {
        String expected = """
                Assembly Resolution No. 459
                                
                CONCURRENT RESOLUTION OF THE SENATE AND ASSEMBLY
                relative to the adjournment
                """;
        String actual = HtmlDiffFormatter.applyWhitespaceOption(rawText, WhitespaceOption.NORMALIZE_WHITESPACE);
        assertEquals(expected, actual);
    }

    @Test
    public void givenWhitespaceOptionREMOVE_WHITESPACE_removeAllNonAlphaNumeric() {
        String expected = """
                AssemblyResolutionNo.459
                                
                CONCURRENTRESOLUTIONOFTHESENATEANDASSEMBLY
                relativetotheadjournment
                """;
        String actual = HtmlDiffFormatter.applyWhitespaceOption(rawText, WhitespaceOption.REMOVE_WHITESPACE);
        assertEquals(expected, actual);
    }

    @Test
    public void givenCharacterOptionALL_CAPS_convertToAllCaps() {
        String expected = """
                ASSEMBLY RESOLUTION NO. 459
                \s
                        CONCURRENT    RESOLUTION    OF    THE    SENATE  AND  ASSEMBLY
                        RELATIVE TO THE ADJOURNMENT
                """;
        String actual = HtmlDiffFormatter.applyCharacterOption(rawText, CharacterOption.ALL_CAPS);
        assertEquals(expected, actual);
    }

    @Test
    public void givenCharacterOptionREMOVE_LINE_NUMBERS_removeAllLineNumbers() {
        // Line numbers prefixed by 3/4 spaces
        String raw = """
                    9  the principal office of which institution  is  located  in  this  state.
                   10  Unless  otherwise  provided  by any provision of this article, or unless
                """;
        String expected = """
                  the principal office of which institution  is  located  in  this  state.
                  Unless  otherwise  provided  by any provision of this article, or unless
                """;
        String actual = HtmlDiffFormatter.applyCharacterOption(raw, CharacterOption.REMOVE_LINE_NUMBERS);
        assertEquals(expected, actual);

        // Line numbers prefixed by 4/5 spaces.
        raw = """
                     9  the principal office of which institution  is  located  in  this  state.
                    10  Unless  otherwise  provided  by any provision of this article, or unless
                """;
        actual = HtmlDiffFormatter.applyCharacterOption(raw, CharacterOption.REMOVE_LINE_NUMBERS);
        assertEquals(expected, actual);
    }
}
