package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SOBIFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing and persisting SOBIFiles.
 */
public interface SOBIFileDao
{
    /** --- Retrieval Methods --- */

    /**
     * Retrieves a SOBIFile object given its file name.
     *
     * @param fileName String - The file name of the SOBI file.
     * @return SOBIFile, null if no matching file name in the database.
     */
    public SOBIFile getSOBIFile(String fileName);

    /**
     * Retrieves a collection of SOBIFiles that match the list of file names.
     * The map will contain a key-value pair for only the file names that were found.
     *
     * @param fileNames List<String> - File names to get SOBIFiles for.
     * @return Map<String, SOBIFile> - Map of file name to SOBIFile
     */
    public Map<String, SOBIFile> getSOBIFiles(List<String> fileNames);

    /**
     * Retrieve a list of SOBIFiles during the given date range.
     *
     * @param start Date - Start of date range (inclusive)
     * @param end Date - End of date range (inclusive)
     * @param processedOnly boolean - If true, only return processed SOBIFiles.
     * @param sortByPubDate SortOrder - Sort order for published date
     * @return List<SOBIFile>
     */
    public List<SOBIFile> getSOBIFilesDuring(Date start, Date end, boolean processedOnly, SortOrder sortByPubDate);

    /**
     * Retrieves the SOBIFiles that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for published date
     * @return List<SOBIFile>
     */
    public List<SOBIFile> getPendingSOBIFiles(SortOrder sortByPubDate);

    /** --- Processing/Insertion Methods --- */

    /**
     * Look in the pre-defined staging directory for new sobi files and designate them as
     * files that are awaiting processing. The files will be moved out of the staging area
     * so that future calls to the method will not see the prior files.
     *
     * Only after a SOBIFile has been staged will it be available to access by file name using
     * the get methods in this interface.
     *
     * @param allowReStaging boolean - If true, a SOBI file with the same file name as one that's
     *                                 been previously staged will get re-staged. Otherwise it will
     *                                 just skip it.
     * @throws java.io.IOException - Might occur if something goes wrong with the file manipulation.
     */
    public void stageSOBIFiles(boolean allowReStaging) throws IOException;

    /**
     * Updates an existing SOBIFile in the backing store with the given instance.
     *
     * @param sobiFile SOBIFile
     * @return boolean - true if record was updated, false if nothing was updated
     */
    public boolean updateSOBIFile(SOBIFile sobiFile);
}
