package gov.nysenate.openleg.dao.hearing;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;

import java.io.IOException;
import java.util.List;

public interface PublicHearingFileDao
{

    /**
     * Get PublicHearingFiles residing in the incoming hearing directory.
     * @param limOff Specifies the maximum number of PublicHearingFiles to fetch.
     * @return List of PublicHearingFile objects.
     * @throws IOException
     * @see gov.nysenate.openleg.model.hearing.PublicHearingFile
     */
    public List<PublicHearingFile> getIncomingPublicHearingFiles(LimitOffset limOff) throws IOException;

    /**
     * Updates the backing store with a given instance or inserts it if the
     * record doesn't already exist.
     * @param publicHearingFile The {@link PublicHearingFile} to update.
     */
    public void updatePublicHearingFile(PublicHearingFile publicHearingFile);

    /**
     * Moves the PublicHearingFile to an archive directory. Ensures that this
     * PublicHearingFile is not processed again by future calls to
     * {@link #getIncomingPublicHearingFiles(gov.nysenate.openleg.dao.base.LimitOffset)}
     * @param publicHearingFile The {@link PublicHearingFile} to archive.
     * @throws IOException
     */
    public void archivePublicHearingFile(PublicHearingFile publicHearingFile) throws IOException;

    /**
     * Retrieves a list of PublicHearingFile that are awaiting processing.
     * @param limOff Specifies the maximum number of PublicHearingFiles to fetch.
     * @return
     */
    public List<PublicHearingFile> getPendingPublicHearingFile(LimitOffset limOff);
}
