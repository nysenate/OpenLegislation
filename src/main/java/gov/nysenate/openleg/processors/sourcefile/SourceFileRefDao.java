package gov.nysenate.openleg.processors.sourcefile;
import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.processors.bill.SourceFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by Robert Bebber on 4/3/17.
 *
 * Stores and retrieves {@link SourceFile} references
 */
public interface SourceFileRefDao {

    /**
     * Updates an existing SourceFile in the backing store with the given instance or inserts it if
     * the record doesn't already exist.
     *
     * @param sourceFile SourceFile - The SourceFile instance to be updated.
     */
    void updateSourceFile(SourceFile sourceFile);

    /**
     * Retrieve the source file with the given name
     * @param sourceFile
     * @return
     */
    SourceFile getSourceFile(String sourceFile);

    /**
     * Get source files with the given names
     * @param fileNames
     * @return
     */
    Map<String, SourceFile> getSourceFiles(List<String> fileNames);

    /**
     * Retrieve a list of archived SobiFiles during the given date/time range.
     *
     * @param dateTimeRange  Range<LocalDateTime> - The range of date/times.
     * @param sortByFileName SortOrder - Sort order for the file name.
     * @param limOff         LimitOffset - Restrict the results list.
     *
     * @return PaginatedList<SobiFile>
     */
    PaginatedList<SourceFile> getSourceFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByFileName,
                                                   LimitOffset limOff);

}
