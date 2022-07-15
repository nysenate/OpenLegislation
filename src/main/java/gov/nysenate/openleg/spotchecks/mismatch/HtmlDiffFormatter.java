package gov.nysenate.openleg.spotchecks.mismatch;

import java.util.Locale;
import java.util.stream.Collectors;

public class HtmlDiffFormatter {

    /**
     * Converts all line endings to be LF endings.
     * @param text This text to convert.
     * @return {@code text} but with LF line endings.
     */
    public static String normalizeLineEndings(String text) {
        return text.replaceAll("\r\n", "\n");
    }

    /**
     * Returned text uses LF for line endings, regardless of what {@code text} originally used.
     *
     * @param text
     * @param option
     * @return
     */
    public static String applyWhitespaceOption(String text, WhitespaceOption option) {
        switch (option) {
            case NORMALIZE_WHITESPACE -> text = text.lines()
                    .map(HtmlDiffFormatter::normalizeWhitespace)
                    .collect(Collectors.joining("\n")) + "\n"; // Add back the last new line.
            case REMOVE_WHITESPACE -> text = text.lines()
                    .map(HtmlDiffFormatter::removeWhitespace)
                    .collect(Collectors.joining("\n")) + "\n"; // Add back the last new line.
        }
        return text;
    }

    private static String normalizeWhitespace(String string) {
        return string.trim().replaceAll(" +", " ");
    }

    private static String removeWhitespace(String string) {
        return string.trim().replaceAll("[ ]+", "");
    }

    public static String applyCharacterOption(String text, CharacterOption option) {
        switch (option) {
            case ALL_CAPS -> text = text.toUpperCase(Locale.ROOT);
            case REMOVE_LINE_NUMBERS -> text = text.lines()
                    .map(HtmlDiffFormatter::removeLineNumbers)
                    .collect(Collectors.joining("\n")) + "\n";
        }
        return text;
    }

    private static String removeLineNumbers(String string) {
        return string.replaceFirst("^\\s{3,4}\\d{2}|^\\s{4,5}\\d", "");
    }
}
