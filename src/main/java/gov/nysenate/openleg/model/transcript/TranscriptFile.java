package gov.nysenate.openleg.model.transcript;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

/**
 * File containing the raw transcript text
 */
public class TranscriptFile
{
    /** Reference to the actual file. */
    private File file;

    /** The datetime this file was last processed. */
    private LocalDateTime processedDateTime;

    /** The datetime when the TranscriptFile was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /** Indicates whether this file has been processed. */
    private boolean pendingProcessing;

    /** The number of times this file has been processed. */
    private int processedCount;

    /** --- Constructors --- */

    public TranscriptFile(File file) throws FileNotFoundException {
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

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
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

    public boolean isPendingProcessing() {
        return pendingProcessing;
    }

    public void setPendingProcessing(boolean pendingProcessing) {
        this.pendingProcessing = pendingProcessing;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }
}
