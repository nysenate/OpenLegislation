package gov.nysenate.openleg.model.xml;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class XmlFile {

    /** Default encoding for XML files (I'm not 100% sure about this. */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /** Pattern of naming convention for xml file. */
    public static final String xmlDateFullPattern = "yyyy'-'MM'-'dd'-'HH'.'mm'.'ss'.'######'.XML'";

    /** Reference to the actual xml file. */
    private File file;

    /** The encoding this file was written in. */
    private String encoding;

    /** The datetime when the XmlFile was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /** Indicates if the file is pending processing */
    private boolean pendingProcessing;

    /** --- Constructors --- */


    public XmlFile(File xmlFile) throws IOException, XmlFileNotFoundEx {
        this(xmlFile, DEFAULT_ENCODING);
    }

    public XmlFile(File file, String encoding) throws IOException, XmlFileNotFoundEx {
        if (file.exists()) {
            this.file = file;
            this.encoding = encoding;
            this.archived = false;
            this.pendingProcessing = true;
            // Attempt to parse the xml file name, raising an exception if the name is invalid
            getPublishedDateTime();
        } else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /** --- Functional Getters/Setters --- */

    /** Returns the file name */
    public String getFileName() {
        return this.file.getName();
    }

    /** Returns the file Id */
    public String getFileId() {

    }

    /** Retrieves the text contained within the file. */
    @JsonIgnore
    public String getText() {

    }

    /**
     * The published datetime is determined via the file name. If an error is encountered when
     * parsing the date, the last modified datetime of the file will be used instead.
     * @throws InvalidXmlNameEx if this xml has a filename that cannot be parsed
     */
    public LocalDateTime getPublishedDateTime() throws InvalidXmlNameEx {

    }

    /** --- Override Methods --- */

    @Override
    public String toString() {

    }

    /** --- Basic Getters/Setters --- */

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }

    public String getEncoding() { return encoding; }

    public LocalDateTime getStagedDateTime() { return stagedDateTime; }

    public void setStagedDateTime(LocalDateTime stagedDateTime) { this.stagedDateTime = stagedDateTime; }

    public boolean isArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }

    public void setPendingProcessing(boolean pendingProcessing) { this.pendingProcessing = pendingProcessing; }

}
