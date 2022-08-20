package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.processors.ProcessService;

import java.util.List;

public interface TranscriptProcessService extends ProcessService {
    /**
     * Looks for Transcript files in the incoming directory, moves them into
     * an archive directory and saves them to the backing store as pending processing.
     *
     * @return int - Number of transcript files collated
     */
    int collateTranscriptFiles();

    /**
     * Reads the content of a TranscriptFile and generates a Transcript object.
     * The Transcript object is saved into the backing store and the TranscriptFile
     * is updated to signify it has been processed.
     *
     * @param transcriptFiles The TranscriptFiles to process.
     * @see Transcript
     * @see TranscriptFile
     */
    int processTranscriptFiles(List<TranscriptFile> transcriptFiles);

    /**
     * Processes all pending TranscriptFiles.
     */
    int processPendingTranscriptFiles();

    /**
     * Toggle the pending processing status of a TranscriptFile.
     *
     * @param transcriptId
     * @param pendingProcessing
     */
    void updatePendingProcessing(TranscriptId transcriptId, boolean pendingProcessing);
}
