package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.processors.BaseSourceData;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

/**
 * File containing the raw transcript text
 */
public class TranscriptFile extends BaseSourceData
{
    /** Reference to the actual file. */
    private File file;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /** Saves original filename for use in database. */
    private String originalFilename;

    /** Used to extract the dateTime, then reused later to not repeat processing. */
    private Transcript transcript;

    /** Used to rename file, and identify unique files. */
    private LocalDateTime dateTime;

    /** --- Constructors --- */

    public TranscriptFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
            this.originalFilename = file.getName();
            setManualFix(originalFilename.endsWith(".fixed"));
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
        this.dateTime = transcript.getDateTime();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
