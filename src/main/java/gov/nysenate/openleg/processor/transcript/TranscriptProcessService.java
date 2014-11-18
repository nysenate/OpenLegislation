package gov.nysenate.openleg.processor.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.processor.base.ProcessService;

import java.util.List;

public interface TranscriptProcessService extends ProcessService
{
    /**
     * Looks for Transcript files in the incoming directory, moves them into
     * an archive directory and saves them to the backing store as pending processing.
     *
     * @return int - Number of transcript files collated
     */
    public int collateTranscriptFiles();

    /**
     * Retrieves a list of TranscriptFiles that are awaiting processing.
     * @param limitOffset Restricts the number retrieved.
     *
     * @return List<TranscriptFile>
     */
    public List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limitOffset);

    /**
     * Reads the content of a TranscriptFile and generates a Transcript object.
     * The Transcript object is saved into the backing store and the TranscriptFile
     * is updated to signify it has been processed.
     *
     * @param transcriptFiles The TranscriptFiles to process.
     * @see gov.nysenate.openleg.model.transcript.Transcript
     * @see gov.nysenate.openleg.model.transcript.TranscriptFile
     */
    public int processTranscriptFiles(List<TranscriptFile> transcriptFiles);

    /**
     * Processes all pending TranscriptFiles via calls to
     * {@link #getPendingTranscriptFiles(gov.nysenate.openleg.dao.base.LimitOffset)}
     * and {@link #processTranscriptFiles(java.util.List)}
     */
    public int processPendingTranscriptFiles();

    /**
     * Toggle the pending processing status of a TranscriptFile.
     *
     * @param transcriptId
     * @param pendingProcessing
     */
    public void updatePendingProcessing(TranscriptId transcriptId, boolean pendingProcessing);
}
