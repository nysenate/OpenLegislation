package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing and persisting SOBIFiles.
 *
 * A typical call stack when processing SOBI files could look like the following:
 * 1. stageSobiFiles(true)                - Stage new files for processing
 * 2. getPendingSobiFiles(SortOrder.ASC)  - Retrieve those new files
 * 3. [ Do some processing ]              - Create and process SOBIFragments somewhere
 * 4. updateSobiFile(..)                  - Update record after processing for the file is done
 */
public interface SobiFileDao
{
    /** --- Retrieval Methods --- */

    /**
     * Retrieves a SobiFile object given its file name.
     *
     * @param fileName String - The file name of the SOBI file.
     * @return SobiFile, null if no matching file name in the database.
     */
    public SobiFile getSobiFile(String fileName);

    /**
     * Retrieves a collection of SOBIFiles that match the list of file names.
     * The map will contain a key-value pair for only the file names that were found.
     *
     * @param fileNames List<String> - File names to get SOBIFiles for.
     * @return Map<String, SobiFile> - Map of file name to SobiFile
     */
    public Map<String, SobiFile> getSobiFiles(List<String> fileNames);

    /**
     * Retrieve a list of SOBIFiles during the given date range.
     *
     * @param start Date - Start of date range (inclusive)
     * @param end Date - End of date range (inclusive)
     * @param processedOnly boolean - If true, only return processed SOBIFiles.
     * @param sortByPubDate SortOrder - Sort order for published date
     * @return List<SobiFile>
     */
    public List<SobiFile> getSobiFilesDuring(Date start, Date end, boolean processedOnly, SortOrder sortByPubDate);

    /**
     * Retrieves the SOBIFiles that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for published date
     * @param limit int - Limit the number of SOBIFiles retrieved, 0 for no limit
     * @param offset int - Used in conjunction with the limit clause, ignored if limit is 0.
     * @return List<SobiFile>
     */
    public List<SobiFile> getPendingSobiFiles(SortOrder sortByPubDate, int limit, int offset);

    /** --- Processing/Insertion Methods --- */

    /**
     * Look in the pre-defined staging directory for new sobi files and designate them as
     * files that are awaiting processing. The files will be moved out of the staging area
     * so that future calls to the method will not see the prior files.
     *
     * Only after a SobiFile has been staged will it be available to access by file name using
     * the get methods in this interface.
     *
     * @param allowReStaging boolean - If true, a SOBI file with the same file name as one that's
     *                                 been previously staged will get re-staged. Otherwise it will
     *                                 just skip it.
     * @throws java.io.IOException - Might occur if something goes wrong with the file manipulation.
     */
    public void stageSobiFiles(boolean allowReStaging) throws IOException;

    /**
     * Updates an existing SobiFile in the backing store with the given instance.
     * If the record doesn't already exist, no action will be taken.
     *
     * @param sobiFile SobiFile
     * @return boolean - true if record was upsdated, false if nothing was updated
     */
    public boolean updateSobiFile(SobiFile sobiFile);
}
