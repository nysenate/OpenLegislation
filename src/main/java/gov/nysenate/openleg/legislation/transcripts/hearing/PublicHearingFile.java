package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.processors.BaseSourceData;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A file containing the raw Public Hearing text.
 */
public class PublicHearingFile extends BaseSourceData
{

    /** A reference to the actual file. */
    private File file;

    /** Indicates if the underlying file reference has been moved into the archive directory. */
    private boolean archived;

    /** --- Constructors --- */

    public PublicHearingFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
            // TODO: change String to a constant in BaseSourceData that everyone can use?
            setManualFix(file.getName().endsWith(".fixed"));
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
