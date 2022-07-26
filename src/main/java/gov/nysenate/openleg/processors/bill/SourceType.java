package gov.nysenate.openleg.processors.bill;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Enumerates types of {@link SourceFile}s
 */
public enum SourceType {
    XML(".*\\.XML"),
    SOBI("SOBI\\.D\\d{6}\\.T\\d{6}\\.TXT");

    private final Predicate<String> filenameMatcher;

    SourceType(String filenameRegex) {
        this.filenameMatcher = Pattern.compile(filenameRegex, Pattern.CASE_INSENSITIVE).asPredicate();
    }

    /* --- Functions --- */

    /**
     * Detects and returns the {@link SourceType} of the given filename.
     * Returns null if the filename does not match any source type patterns
     * @param filename
     * @return {@link SourceType}
     */
    public static SourceType ofFile(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Received null filename");
        }
        return Arrays.stream(SourceType.values())
                .filter(sourceType -> sourceType.filenameMatcher.test(filename))
                .findFirst()
                .orElse(null);
    }
}
