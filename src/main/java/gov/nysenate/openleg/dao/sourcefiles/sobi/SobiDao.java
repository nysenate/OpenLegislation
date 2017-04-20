package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.Range;

import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import org.springframework.dao.DataAccessException;

import java.io.File;
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

    void archiveSobiFile(SourceFile sobiFile) throws IOException;

    File getFileInIncomingDir(String fileName);

    File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime);
}
