package gov.nysenate.openleg.model.sobixml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the 'newer' source files that LBDC sends.
 */
public class SobiXmlFile
{
    private static final Pattern fileNamePattern =
        Pattern.compile("(?<date>[0-9-]{10})-(?<time>[0-9.]{15})_(?<type>[A-Z]+)_(?<target>.+)\\.XML");

    /** Reference to the actual sobi file. */
    private File file;

    /** The datetime when the SobiFile was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /** --- Constructors --- */

    public SobiXmlFile(File file) throws IOException {
        if (file.exists()) {
            this.file = file;
            this.archived = false;
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /** --- Functional Getters --- */

    /**
     * The file name serves as the unique identifier for the SobiXmlFile.
     */
    public String getFileName() {
        return this.file.getName();
    }

    /**
     * Retrieves the text contained within the file.
     */
    @JsonIgnore
    public String getText() {
        try {
            return FileUtils.readFileToString(file);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read text from SobiFile:" + this.toString());
        }
    }

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
            throw new SobiXmlException("Failed to parse published datetime from Sobi XML: " + ex.getMessage());
        }
        throw new SobiXmlException(
            "Failed to parse published datetime from Sobi XML because the filename" +
            " did not match the required format.");
    }

    /** --- Basic Getters/Setters --- */

    public File getFile() {
        return file;
    }

    public LocalDateTime getStagedDateTime() {
        return stagedDateTime;
    }

    public void setStagedDateTime(LocalDateTime stagedDateTime) {
        this.stagedDateTime = stagedDateTime;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}