package gov.nysenate.openleg.dao.xml;


import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * The XmlDao for retrieving and modifying XmlFiles. This interface is only converned with the storage mechanisms of the
 * xml data and does not perform any parsing related to the content of the data.
 */
public interface XmlDao {

    /** --- Retrieval Methods --- */

    /**
     * Retrieves an archived xml file with the given name.
     *
     * @param fileName - Name of the xml file to be retrieved.
     * @return XmlFile
     * @throws DataAccessException - If there was an error while retrieving the XmlFile.
     */
    public XmlFile getXmlFile(String fileName) throws DataAccessException);

    /**
     * Retrieves a collection of archived XmlFiles that match the given list of names.
     *
     * @param fileNames - The list of the file names to the retrieved.
     * @return Map<String, XmlFile> - A mapping of the given names to their respective xml files.
     */
    public Map<String, XmlFile> getXmlFiles(List<String> fileNames);

    /**
     * Retrieves a list of archived XmlFiles during the given date/time range.
     *
     * @param dateTimeRange - The range of date/times.
     * @param sortByFileName - Sort order for the file name.
     * @param limitOffset - Restrict the results list.
     * @return PaginatedList<XmlFile>
     */
    public PaginatedList<XmlFile> getXmlFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByFileName,
                                                    LimitOffset limitOffset);

    /**
     * Returns a list of the xml files in the incoming directory.
     * These are the files that have yet to be processed.
     *
     * @param sortByFileName - Sort order for the file name.
     * @param limitOffset - Restrict the results list.
     * @return List<XmlFile>
     * @throws IOException - If there was a problem with handling the files.
     */
    public List<XmlFile> getIncomingXmlFiles(SortOrder sortByFileName, LimitOffset limitOffset) throws IOException;

    /**
     * Retrieves all XmlFiles that are awaiting processing.
     *
     * @param sortById - Sort order for the file id.
     * @param limitOffset - Restrict the results list.
     * @return List<XmlFile>
     */
    public List<XmlFile> getPendingXmlFiles(SortOrder sortById, LimitOffset limitOffset);

    /**
     * Retrieves the XmlFiles that are awaiting processing and belong to one of the types
     * in the given 'restrict' set.
     *
     *
     * @param restrict - Filter result set to only include these types.
     * @param sortById - Sort order for the fragment id.
     * @param limitOffset - Restrict the results list.
     * @return List<XmlFile>
     */
    public List<XmlFile> getPendingXmlFiles(ImmutableSet<XmlFileType> restrict, SortOrder sortById,
                                            LimitOffset limitOffset);

    /** --- Update/Insert Methods --- */

    /**
     * Moves the given file to the archive directory. This ensures that calls to getIncomingXmlFiles will not
     * return this file.
     * {@link #updateXmlFile(XmlFile)} method is invoked as part of this process to ensure consistency.
     *
     * @param xmlFile - The file to be archived.
     * @throws IOException - If there was a problem in moving the file.
     */
    public void archiveAndUpdateXmlFile(XmlFile xmlFile) throws IOException;

    /**
     * Updates an existing XmlFile in the backing store with the given instance or inserts it if
     * the record doesn't already exist.
     *
     * @param xmlFile - The file to be updated.
     */
    public void updateXmlFile(XmlFile xmlFile);

}
