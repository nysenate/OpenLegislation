package gov.nysenate.openleg.model.sobi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The SobiFile class wraps the sobi files sent from LBDC and retains some basic meta data.
 * SobiFiles can be broken down into SobiFragments which store data about the type of content in
 * the file and various processing related meta data.
 *
 * @see SobiFragment
 */
public class SobiFile
{
    /**
     * SOBI files are (mostly) in a CP850 or similar encoding. This was determined from the byte mapping of
     * paragraph/section characters to 244/245. This can't be 100% correct though because the degree symbol
     * must be 193 in the correct code set. See SOBI.D120612.T125850.TXT.
     */
    public static final String DEFAULT_ENCODING = "CP850";

    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    private static final String sobiDateFullPattern = "'SOBI.D'yyMMdd'.T'HHmmss'.TXT'";

    /** Alternate format for SOBI files with no seconds specified in the filename */
    private static final String sobiDateNoSecsPattern = "'SOBI.D'yyMMdd'.T'HHmm'.TXT'";

    /** Reference to the actual sobi file. */
    private File file;

    /** The encoding this file was written in. */
    private String encoding;

    /** The datetime when the SobiFile was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /** --- Constructors --- */

    public SobiFile(File sobiFile) throws IOException {
        this(sobiFile, DEFAULT_ENCODING);
    }

    public SobiFile(File file, String encoding) throws IOException {
        if (file.exists()) {
            this.file = file;
            this.encoding = encoding;
            this.archived = false;
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /** --- Functional Getters/Setters --- */

    /**
     * The file name serves as the unique identifier for the SobiFile.
     */
    public String getFileName() {
        return this.file.getName();
    }

    /**
     * Retrieves the text contained within the file. The text is not saved due to the
     * added memory overhead when retaining references to SobiFiles.
     */
    @JsonIgnore
    public String getText() {
        try {
            return FileUtils.readFileToString(file, encoding);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read text from SobiFile:" + this.toString());
        }
    }

    /**
     * The published datetime is determined via the file name. If an error is encountered when
     * parsing the date, the last modified datetime of the file will be used instead.
     */
    public LocalDateTime getPublishedDateTime() {
        try {
            return LocalDateTime.ofInstant(
                org.apache.commons.lang3.time.DateUtils.parseDate(getFileName(), sobiDateFullPattern, sobiDateNoSecsPattern).toInstant(),
                ZoneId.systemDefault());
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            return DateUtils.getLocalDateTimeFromMillis(file.lastModified());
        }
    }

    /** --- Override Methods --- */

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("file", file)
            .add("encoding", encoding)
            .add("stagedDateTime", stagedDateTime)
            .add("archived", archived)
            .toString();
    }

    /** --- Basic Getters/Setters --- */

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getEncoding() {
        return encoding;
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