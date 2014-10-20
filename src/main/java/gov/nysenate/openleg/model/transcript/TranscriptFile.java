package gov.nysenate.openleg.model.transcript;

import gov.nysenate.openleg.model.base.BaseSourceData;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * File containing the raw transcript text
 */
public class TranscriptFile extends BaseSourceData
{
    /** Reference to the actual file. */
    private File file;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
