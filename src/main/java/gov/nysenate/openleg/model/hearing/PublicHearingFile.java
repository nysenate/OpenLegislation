package gov.nysenate.openleg.model.hearing;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

/**
 * A file containing the raw Public Hearing text.
 */
public class PublicHearingFile
{

    /** A reference to the actual file. */
    private File file;

    /** The date time this Public Hearing File was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** The date time this Public Hearing File was last processed. */
    private LocalDateTime processedDateTime;

    /** The number of times this Public Hearing File has been processed. */
    private int processedCount;

    /** Indicates if this file has been processed. */
    private boolean pendingProcessing;

    /** Indicates if the underlying file reference has been moved into the archive directory. */
    private boolean archived;

    /** --- Constructors --- */

    public PublicHearingFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /** --- Functional Getters/Setters --- */

    public String getFileName() {
        return file.getName();
    }

    /** --- Basic Getters/Setters --- */

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public LocalDateTime getStagedDateTime() {
        return stagedDateTime;
    }

    public void setStagedDateTime(LocalDateTime stagedDateTime) {
        this.stagedDateTime = stagedDateTime;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public boolean isPendingProcessing() {
        return pendingProcessing;
    }

    public void setPendingProcessing(boolean pendingProcessing) {
        this.pendingProcessing = pendingProcessing;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
