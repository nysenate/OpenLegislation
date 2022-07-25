package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.processors.ProcessService;

import java.util.List;

public interface HearingProcessService extends ProcessService {
    /**
     * Looks for Public Hearing Files in the incoming directory, moves them
     * into an archive directory and saves them to the backing store as pending processing.
     * @return
     */
    int collateHearingFiles();

    /**
     * Retrieves a list of Public Hearing Files that are awaiting processing.
     * @param limitOffset Restricts the number retrieved.
     * @return
     */
    List<HearingFile> getPendingHearingFiles(LimitOffset limitOffset);

    /**
     * Reads the content of a Public Hearing File and generates a Hearing object.
     * The Hearing object is saved into the backing store and the Public Hearing File
     * is updated to signify it has been processed.
     * @param hearingFiles The HearingFile to process.
     * @see Hearing
     */
    int processHearingFiles(List<HearingFile> hearingFiles);

    /**
     * Processes all the Hearing Files via calls to
     * {@link #getPendingHearingFiles(LimitOffset)}
     * and {@link #processHearingFiles(java.util.List)}.
     */
    int processHearingFiles();

    /**
     * Toggle the pending processing status of a Public Hearing File.
     * @param hearingId
     * @param pendingProcessing
     */
    void updatePendingProcessing(HearingId hearingId, boolean pendingProcessing);
}
