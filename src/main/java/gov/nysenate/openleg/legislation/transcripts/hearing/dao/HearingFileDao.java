package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;

import java.io.IOException;
import java.util.List;

public interface HearingFileDao {
    /**
     * Get HearingFiles residing in the incoming hearing directory.
     * @param limOff Specifies the maximum number of HearingFiles to fetch.
     * @return List of HearingFile objects.
     * @throws IOException
     * @see HearingFile
     */
    List<HearingFile> getIncomingHearingFiles(LimitOffset limOff) throws IOException;

    /**
     * Updates the backing store with a given instance or inserts it if the
     * record doesn't already exist.
     * @param hearingFile The {@link HearingFile} to update.
     */
    void updateHearingFile(HearingFile hearingFile);

    /**
     * Moves the HearingFile to an archive directory. Ensures that this
     * HearingFile is not processed again by future calls to
     * {@link #getIncomingHearingFiles(LimitOffset)}
     * @param hearingFile The {@link HearingFile} to archive.
     * @throws IOException
     */
    void archiveHearingFile(HearingFile hearingFile) throws IOException;

    /**
     * Retrieves a list of HearingFile that are awaiting processing.
     * @param limOff Specifies the maximum number of HearingFiles to fetch.
     * @return
     */
    List<HearingFile> getPendingHearingFile(LimitOffset limOff);
}
