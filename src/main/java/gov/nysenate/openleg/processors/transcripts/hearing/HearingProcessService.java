package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.processors.ProcessService;

import java.util.List;

public interface HearingProcessService extends ProcessService {
    /**
     * Looks for Hearing Files in the incoming directory, moves them
     * into an archive directory and saves them to the backing store as pending processing.
     * @return
     */
    int collateHearingFiles();

    /**
     * Reads the content of a Hearing File and generates a Hearing object.
     * The Hearing object is saved into the backing store and the HearingFile
     * is updated to signify it has been processed.
     * @param hearingFiles The HearingFile to process.
     * @see Hearing
     */
    int processHearingFiles(List<HearingFile> hearingFiles);

    /**
     * Processes all the Hearing Files.
     * and {@link #processHearingFiles(java.util.List)}.
     */
    int processHearingFiles();

    /**
     * Toggle the pending processing status of a Hearing File.
     * @param hearingId
     * @param pendingProcessing
     */
    void updatePendingProcessing(HearingId hearingId, boolean pendingProcessing);
}
