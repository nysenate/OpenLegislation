package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.Range;

import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;

/**
 * The SobiDao interface exposes methods for retrieving and modifying SobiFiles and
 * their respective SobiFragments. This interface is only concerned with the storage
 * mechanisms of the sobi data and does not perform any parsing related to the content
 * of the data.
 */
public interface SobiDao {
    /** --- Retrieval Methods --- */
    
    /**
     * Retrieves an archived SobiFile object with the given file name.
     *
     * @param fileName String - The file name of the SOBI file.
     *
     * @return SobiFile
     *
     * @throws DataAccessException - If there was an error while retrieving the SobiFile.
     */
    SobiFile getSobiFile(String fileName) throws DataAccessException;
    
    /**
     * Retrieves a collection of archived SobiFiles that match the list of file names.
     * The map will contain a key-value pair for only the file names that were found.
     *
     * @param fileNames List<String> - File names to get SobiFiles for.
     *
     * @return Map<String, SobiFile> - Map of file name to SobiFile
     */
    Map<String, SobiFile> getSobiFiles(List<String> fileNames);
    
    /**
     * Retrieve a list of archived SobiFiles during the given date/time range.
     *
     * @param dateTimeRange  Range<LocalDateTime> - The range of date/times.
     * @param sortByFileName SortOrder - Sort order for the file name.
     * @param limOff         LimitOffset - Restrict the results list.
     *
     * @return PaginatedList<SobiFile>
     */
    PaginatedList<SobiFile> getSobiFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByFileName,
                                               LimitOffset limOff);
    
    /**
     * Returns SobiFile instances of the files residing in the incoming sobis directory.
     * These are basically SobiFiles that have not yet been processed.
     *
     * @param sortByFileName SortOrder - Sort order for the file name.
     * @param limOff         LimitOffset - Restrict the results list.
     *
     * @return List<SobiFile>
     *
     * @throws IOException - If there was a problem with handling the files.
     */
    List<SobiFile> getIncomingSobiFiles(SortOrder sortByFileName, LimitOffset limOff) throws IOException;
    
    /** --- Update/Insert Methods --- */
    
    /**
     * Moves the underlying file in the SobiFile instance into the archive directory. This will
     * ensure that subsequent calls to getIncomingSobiFiles will not return this sobiFile. The
     * {@link #updateSobiFile(SobiFile)} method is invoked as part of this process to ensure consistency.
     *
     * @param sobiFile SobiFile - The SobiFile instance to be archived.
     *
     * @throws IOException - If there was a problem in moving the underlying file.
     */
    void archiveAndUpdateSobiFile(SobiFile sobiFile) throws IOException;
    
    /**
     * Updates an existing SobiFile in the backing store with the given instance or inserts it if
     * the record doesn't already exist.
     *
     * @param sobiFile SobiFile - The SobiFile instance to be updated.
     */
    void updateSobiFile(SobiFile sobiFile);
}
