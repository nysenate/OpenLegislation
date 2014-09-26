package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.transcript.TranscriptFile;

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
    public List<TranscriptFile> getIncomingTranscriptFiles(LimitOffset limOff) throws IOException;

    /**
     * Retrieves a list of TranscriptFiles that are awaiting processing.
     * i.e. {@link TranscriptFile#pendingProcessing} is true.
     *
     * @param limOff Specifies the maximum number of TranscriptFiles to fetch
     * @return List<TranscriptFile>
     */
    public List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limOff);

    /**
     * Updates the backing store with the given instance or inserts it
     * if the record doesn't already exist.
     *
     * @param transcriptFile The {@link TranscriptFile} instance to be updated.
     */
    public void updateTranscriptFile(TranscriptFile transcriptFile);

    /**
     * Moves the TranscriptFile to an archived directory. Ensures that this TranscriptFile is not processed
     * again by future calls to {@link #getIncomingTranscriptFiles(LimitOffset)}.
     *
     * @param transcriptFile The TranscriptFile instance to be archived.
     * @throws IOException
     * @see TranscriptFile
     */
    public void archiveAndUpdateTranscriptFile(TranscriptFile transcriptFile) throws IOException;
}