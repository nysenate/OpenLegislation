package gov.nysenate.openleg.legislation.transcripts;

import java.io.IOException;
import java.util.List;

public interface TranscriptFileDaoInterface<T extends AbstractTranscriptsFile> {
    /**
     * Get files residing in the incoming hearing directory.
     * @return List of {@link AbstractTranscriptsFile} objects.
     * @throws IOException Thrown if the files cannot be accessed.
     */
    List<T> getIncomingFiles() throws IOException;

    /**
     * Updates the backing store with a given instance or inserts it if the
     * record doesn't already exist.
     * @param file The {@link AbstractTranscriptsFile} to update.
     */
    void updateFile(T file);

    /**
     * Moves the file to an archive directory. Ensures that this
     * file is not processed again by future calls to
     * {@link #getIncomingFiles()}
     * @param hearingFile The {@link AbstractTranscriptsFile} to archive.
     * @throws IOException if there is a problem accessing the files.
     */
    void archiveFile(T hearingFile) throws IOException;

    /**
     * Retrieves a list of files that are awaiting processing.
     */
    List<T> getPendingFiles();
}
