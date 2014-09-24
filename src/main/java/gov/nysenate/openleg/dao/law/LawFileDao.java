package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.law.LawFile;

import java.io.IOException;
import java.util.List;

public interface LawFileDao
{
    /**
     * Returns LawFile instances for the source files in the incoming laws directory.
     * These are basically law files that have not yet been processed.
     *
     * @param sortByDate SortOrder - Order by the published date or the filename if there is a tie.
     * @param limitOffset LimitOffset - Limit the result set.
     * @return List<LawFile>
     * @throws IOException - If there was a problem with handling the files.
     */
    public List<LawFile> getIncomingLawFiles(SortOrder sortByDate, LimitOffset limitOffset) throws IOException;

    /**
     * Updates an existing LawFile in the backing store with the given instance or inserts it if
     * the record doesn't already exist.
     *
     * @param lawFile LawFile - The LawFile instance to be updated.
     * @throws IOException
     */
    public void updateLawFile(LawFile lawFile) throws IOException;

    /**
     * Moves the underlying file in the LawFile instance into the archive directory. This will
     * ensure that subsequent calls to getIncomingLawFiles will not return this lawFile. The
     * {@link #updateLawFile(LawFile)} method is invoked as part of this process to ensure consistency.
     *
     * @param lawFile
     * @throws IOException
     */
    public void archiveAndUpdateLawFile(LawFile lawFile) throws IOException;
}