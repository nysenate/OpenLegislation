package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.AbstractTranscriptsFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

/**
 * File containing the raw transcript text
 */
public class TranscriptFile extends AbstractTranscriptsFile {
    /** Saves original filename for use in database. */
    private String originalFilename;

    /** Should be updated once Transcript is parsed. */
    private LocalDateTime dateTime;

    /** --- Constructors --- */

    public TranscriptFile(File file) throws FileNotFoundException {
        super(file);
        this.originalFilename = file.getName();
    }

    /** --- Basic Getters/Setters --- */

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setDateTime(LocalDateTime ldt) {
        this.dateTime = ldt;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
