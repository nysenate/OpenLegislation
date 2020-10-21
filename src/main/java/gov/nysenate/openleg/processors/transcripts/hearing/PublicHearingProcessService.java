package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.processors.ProcessService;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

import java.util.List;

public interface PublicHearingProcessService extends ProcessService
{

    /**
     * Looks for Public Hearing Files in the incoming directory, moves them
     * into an archive directory and saves them to the backing store as pending processing.
     * @return
     */
    int collatePublicHearingFiles();

    /**
     * Retrieves a list of Public Hearing Files that are awaiting processing.
     * @param limitOffset Restricts the number retrieved.
     * @return
     */
    List<PublicHearingFile> getPendingPublicHearingFiles(LimitOffset limitOffset);

    /**
     * Reads the content of a Public Hearing File and generates a PublicHearing object.
     * The PublicHearing object is saved into the backing store and the Public Hearing File
     * is updated to signify it has been processed.
     * @param publicHearingFiles The PublicHearingFile to process.
     * @see PublicHearing
     */
    int processPublicHearingFiles(List<PublicHearingFile> publicHearingFiles);

    /**
     * Processes all the Public Hearing Files via calls to
     * {@link #getPendingPublicHearingFiles(LimitOffset)}
     * and {@link #processPublicHearingFiles(java.util.List)}.
     */
    int processPendingPublicHearingFiles();

    /**
     * Toggle the pending processing status of a Public Hearing File.
     * @param publicHearingId
     * @param pendingProcessing
     */
    void updatePendingProcessing(PublicHearingId publicHearingId, boolean pendingProcessing);
}
