package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;

import java.io.IOException;
import java.util.List;

public interface PublicHearingFileDao
{

    /**
     * Get PublicHearingFiles residing in the incoming hearing directory.
     * @param limOff Specifies the maximum number of PublicHearingFiles to fetch.
     * @return List of PublicHearingFile objects.
     * @throws IOException
     * @see PublicHearingFile
     */
    List<PublicHearingFile> getIncomingPublicHearingFiles(LimitOffset limOff) throws IOException;

    /**
     * Updates the backing store with a given instance or inserts it if the
     * record doesn't already exist.
     * @param publicHearingFile The {@link PublicHearingFile} to update.
     */
    void updatePublicHearingFile(PublicHearingFile publicHearingFile);

    /**
     * Moves the PublicHearingFile to an archive directory. Ensures that this
     * PublicHearingFile is not processed again by future calls to
     * {@link #getIncomingPublicHearingFiles(LimitOffset)}
     * @param publicHearingFile The {@link PublicHearingFile} to archive.
     * @throws IOException
     */
    void archivePublicHearingFile(PublicHearingFile publicHearingFile) throws IOException;

    /**
     * Retrieves a list of PublicHearingFile that are awaiting processing.
     * @param limOff Specifies the maximum number of PublicHearingFiles to fetch.
     * @return
     */
    List<PublicHearingFile> getPendingPublicHearingFile(LimitOffset limOff);
}
