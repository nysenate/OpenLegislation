package gov.nysenate.openleg.model.sobixml;

import gov.nysenate.openleg.model.sobi.SobiFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the 'newer' source files that LBDC sends.
 */
public class SobiXmlFile extends SobiFile
{
    private static final Pattern fileNamePattern =
        Pattern.compile("(?<date>[0-9-]{10})-(?<time>[0-9.]{15})_(?<type>[A-Z]+)_(?<target>.+)\\.XML");

    /** --- Constructors --- */

    public SobiXmlFile(File sobiFile) throws IOException {
        super(sobiFile);
    }

    public SobiXmlFile(File file, String encoding) throws IOException {
        super(file, encoding);
    }

    /** --- Overrides --- */

    /**
     * Get the published date time from the file name.
     * @return LocalDateTime
     */
    public LocalDateTime getPublishedDateTime() {
        try {
            Matcher m = fileNamePattern.matcher(getFileName());
            if (m.matches()) {
                return LocalDateTime.parse(m.group("date")  + "T" + m.group("time"));
            }
        }
        catch (DateTimeParseException ex) {
            throw new IllegalStateException("Failed to parse published datetime from Sobi XML: " + ex.getMessage());
        }
        throw new IllegalStateException(
                "Failed to parse published datetime from Sobi XML because the filename" +
                        " did not match the required format.");
    }
}