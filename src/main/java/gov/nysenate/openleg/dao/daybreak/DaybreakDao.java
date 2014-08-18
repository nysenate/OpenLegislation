package gov.nysenate.openleg.dao.daybreak;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * A DaybreakDao allows for the storage and retrieval of Daybreak files
 */
public interface DaybreakDao
{
    /** --- Retrieval Methods --- */

    /**
     * Retrieves an archived Daybreak File object with the given filename, from the given report
     *
     * @param reportDate - The date of the report that contains the desired daybreak file
     * @param fileName - the name of the daybreak file
     * @return DaybreakFile
     */
    public DaybreakFile getDaybreakFile(LocalDate reportDate, String fileName);

    /**
     * Retrieves an archived Daybreak File object of the given file type, from the given report
     *
     * @param reportDate - The date of the report that contains the desired daybreak file
     * @param fileType - the desired daybreak file type
     * @return DaybreakFile
     */
    public DaybreakFile getDaybreakFile(LocalDate reportDate, DaybreakDocType fileType);

    /**
     * Retrieves a set of archived daybreak files from the given report date
     *
     * @param reportDate - The date of the report that contains the desired daybreak files
     * @return Map<DaybreakFileType, DaybreakFile>
     */
    public DaybreakReport<DaybreakFile> getDaybreakReport(LocalDate reportDate);

    /**
     * Gets all DaybreakFiles from the incoming directory
     *
     * @throws java.io.IOException
     * @return List<DaybreakFile>
     */
    public DaybreakReportSet<DaybreakFile> getIncomingReports() throws IOException;

    /**
     * Retrieves a DaybreakFragment for the given BillId from the report on the given date
     *
     * @param daybreakFragmentId - The id for the desired fragment
     * @return DaybreakFragment
     */
    public DaybreakFragment getDaybreakFragment(DaybreakFragmentId daybreakFragmentId);

    /**
     * Retrieves al DaybreakFragments for the report on the given date
     * @param reportDate - The date of the report that contains the desired daybreak fragments
     * @return List<DaybreakFragment>
     */
    public List<DaybreakFragment> getDaybreakFragments(LocalDate reportDate);

    /**
     * Retrieves all DaybreakFragments that have not yet been processed
     *
     * @return List<DaybreakFragment>
     */
    public List<DaybreakFragment> getPendingDaybreakFragments();

    /**
     * Retrieves all pagefile entries that would correspond do a single daybreak fragment
     * @param daybreakFragmentId
     * @return List<PageFileEntry>
     */
    public Map<BillId, PageFileEntry> getPageFileEntries(DaybreakFragmentId daybreakFragmentId);

    /**
     * Retrieves all pagefile entries for a single report
     * @param reportDate
     * @return
     */
    public Map<BaseBillId, Map<BillId, PageFileEntry>> getAllPageFileEntries(LocalDate reportDate);

    /** --- Update/Insert Methods --- */

    /**
     * Moves the file pointed at by the given Daybreak File to the archived directory
     *  so that getIncomingReports will no longer return this DaybreakFile
     * @param daybreakFile
     * @throws IOException
     */
    public void archiveDaybreakFile(DaybreakFile daybreakFile) throws IOException;

    /**
     * Updates or inserts the given DaybreakFile
     * @param daybreakFile
     */
    public void updateDaybreakFile(DaybreakFile daybreakFile);

    /**
     * Updates or inserts the given DaybreakFragment
     * @param daybreakFragment
     */
    public void updateDaybreakFragment(DaybreakFragment daybreakFragment);

    /**
     * Updates or inserts the given PageFileEntry
     * @param pageFileEntry
     */
    public void updatePageFileEntry(PageFileEntry pageFileEntry);

    /** --- Delete Methods --- */

    /**
     * Deletes all page file entries for the report on the given date
     * @param reportDate
     */
    public void deletePageFileEntries(LocalDate reportDate);
}
