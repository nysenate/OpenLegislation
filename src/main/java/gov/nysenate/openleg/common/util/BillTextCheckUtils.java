package gov.nysenate.openleg.common.util;

import java.util.Arrays;
import java.util.Optional;

public final class BillTextCheckUtils {
    private static final String lineNumberRegex = "(?:^( {4}\\d| {3}\\d\\d))";
    private static final String pageMarkerRegex = "^ {7}[A|S]\\. \\d+(--[A-Z])?[ ]+\\d+([ ]+[A|S]\\. \\d+(--[A-Z])?)?$";
    private static final String budgetPageMargerRegex = "^[ ]{42,43}\\d+[ ]+\\d+-\\d+-\\d+$";
    private static final String explanationRegex = "^[ ]+EXPLANATION--Matter in ITALICS \\(underscored\\) is new; matter in brackets\\n";
    private static final String explanationRegex2 = "^[ ]+\\[ ] is old law to be omitted.\\n[ ]+LBD\\d+-\\d+-\\d+$";
    private static final String ultraNormalizeRegex = "(?m)" + String.join("|", Arrays.asList(
            lineNumberRegex, pageMarkerRegex, budgetPageMargerRegex, explanationRegex, explanationRegex2));

    private BillTextCheckUtils() {}

    /**
     * Performs a simple normalization to eliminate potential for mismatches that we would never care about.
     *
     * Removes all non alpha characters
     * Replace section symbol(§) with S
     * CAPITALIZE EVERYTHING.
     */
    public static String basicNormalize(String text) {
        return Optional.ofNullable(text).orElse("")
                .replaceAll("§", "S")
                .replaceAll("(?:[^\\w]|_)+", "")
                .toUpperCase();
    }

    /**
     * Performs a more advanced normalization of text,
     * removing specific sections that do not contribute to overall content.
     *
     * Removes all whitespace, line numbers, and page numbers
     * also performs {@link #basicNormalize(String)}
     */
    public static String ultraNormalize(String text) {
        String stripped = Optional.ofNullable(text).orElse("").replaceAll(ultraNormalizeRegex, "");
        return basicNormalize(stripped);
    }
}
