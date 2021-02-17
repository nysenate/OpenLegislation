package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;

import java.io.IOException;
import java.util.List;

public interface TranscriptFileDao
{
    /**
     * Get TranscriptFiles residing in the incoming transcripts directory.
     *
     * @param limOff Specifies the maximum number of TranscriptFiles to fetch
     * @return List of TranscriptFile objects
     * @throws IOException
     * @see TranscriptFile
     */
    List<TranscriptFile> getIncomingTranscriptFiles(LimitOffset limOff) throws IOException;

    /**
     * Retrieves a list of TranscriptFiles that are awaiting processing.
     *
     * @param limOff Specifies the maximum number of TranscriptFiles to fetch
     * @return List<TranscriptFile>
     */
    List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limOff);

    /**
     * Updates the backing store with the given instance or inserts it
     * if the record doesn't already exist.
     *
     * @param transcriptFile The {@link TranscriptFile} instance to be updated.
     */
    void updateTranscriptFile(TranscriptFile transcriptFile);

    /**
     * Moves the TranscriptFile to an archived directory. Ensures that this TranscriptFile is not processed
     * again by future calls to {@link #getIncomingTranscriptFiles(LimitOffset)}.
     *
     * @param transcriptFile The TranscriptFile instance to be archived.
     * @throws IOException
     * @see TranscriptFile
     */
    void archiveAndUpdateTranscriptFile(TranscriptFile transcriptFile) throws IOException;
}