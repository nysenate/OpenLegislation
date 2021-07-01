package gov.nysenate.openleg.legislation.transcripts;

import gov.nysenate.openleg.processors.BaseSourceData;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class AbstractTranscriptsFile extends BaseSourceData {
    /** Reference to the actual file. */
    protected File file;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    protected boolean archived;

    protected AbstractTranscriptsFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
            setManualFix(file.getName().endsWith(".fixed"));
        }
        else
            throw new FileNotFoundException(file.getAbsolutePath());
    }

    /** --- Basic Getters/Setters --- */

    public String getFileName() {
        return file.getName();
    }

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
