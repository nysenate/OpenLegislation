package gov.nysenate.openleg.processors.sourcefile;


import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.processors.bill.SourceFile;
import gov.nysenate.openleg.processors.bill.SourceType;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The SourceFileFsDao interface exposes methods for retrieving and modifying Source Files.
 * This interface is only concerned with the storage
 * mechanisms of the source data and does not perform any parsing related to the content
 * of the data.
 */
public interface SourceFileFsDao<FileType extends SourceFile> {

    /**
     * @return {@link SourceType} the source file type managed by this dao
     */
    SourceType getSourceType();

    /**
     * Returns SourceFile instances of the files residing in the incoming directory.
     * These are basically SourceFiles that have not yet been processed.
     *
     * @param sortByFileName SortOrder - Sort order for the file name.
     * @param limOff         LimitOffset - Restrict the results list.
     *
     * @return List<FileType>
     *
     * @throws IOException - If there was a problem with handling the files.
     */
    List<FileType> getIncomingSourceFiles(SortOrder sortByFileName, LimitOffset limOff) throws IOException;

    /**
     * Archives the given source file, ensuring that it is no longer in the incoming directory
     * and placing it in the archive directory
     * @param sourceFile
     * @throws IOException
     */
    void archiveSourceFile(FileType sourceFile) throws IOException;

    /**
     * Get a file from the incoming directory by name.
     *
     * @param fileName
     * @return File
     */
    File getFileInIncomingDir(String fileName);

    /**
     * Get a file from the archive directory by name and published date time
     *
     * @param fileName
     * @param publishedDateTime
     * @return
     */
    File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime);
}
