package gov.nysenate.openleg.processors.bill;

import gov.nysenate.openleg.processors.bill.sobi.SobiFile;
import gov.nysenate.openleg.processors.bill.xml.XmlFile;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Enumerates types of {@link SourceFile}s
 */
public enum SourceType {
    XML(XmlFile.class, ".*\\.XML"),
    SOBI(SobiFile.class, "SOBI\\.D\\d{6}\\.T\\d{6}\\.TXT")
    ;

    private Class<? extends SourceFile> fileClass;
    private Predicate<String> filenameMatcher;

    SourceType(Class<? extends SourceFile> fileClass, String filenameRegex) {
        this.fileClass = fileClass;
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

    /* --- Getters --- */

    public Class<? extends SourceFile> getFileClass() {
        return fileClass;
    }
}
