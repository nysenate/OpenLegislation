package gov.nysenate.openleg.dao.bill.reference.daybreak;

import com.google.common.collect.Range;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.*;
import org.springframework.dao.DataAccessException;

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
    public DaybreakFile getDaybreakFile(LocalDate reportDate, String fileName) throws DataAccessException;

    /**
     * Retrieves an archived Daybreak File object of the given file type, from the given report
     *
     * @param reportDate - The date of the report that contains the desired daybreak file
     * @param fileType - the desired daybreak file type
     * @return DaybreakFile
     */
    public DaybreakFile getDaybreakFile(LocalDate reportDate, DaybreakDocType fileType) throws DataAccessException;

    /**
     * Retrieves a set of archived daybreak files from the given report date
     *
     * @param reportDate - The date of the report that contains the desired daybreak files
     * @return Map<DaybreakFileType, DaybreakFile>
     */
    public DaybreakReport<DaybreakFile> getDaybreakReport(LocalDate reportDate) throws DataAccessException;

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
     * @param daybreakBillId - The id for the desired fragment
     * @return DaybreakFragment
     */
    public DaybreakFragment getDaybreakFragment(DaybreakBillId daybreakBillId) throws DataAccessException;

    /**
     * Retrieves al DaybreakFragments for the report on the given date
     * @param reportDate - The date of the report that contains the desired daybreak fragments
     * @return List<DaybreakFragment>
     */
    public List<DaybreakFragment> getDaybreakFragments(LocalDate reportDate) throws DataAccessException;

    /**
     * Retrieves all DaybreakFragments that have not yet been processed
     *
     * @return List<DaybreakFragment>
     */
    public List<DaybreakFragment> getPendingDaybreakFragments();

    /**
     * Retrieves all pagefile entries that would correspond do a single daybreak fragment
     * @param daybreakBillId
     * @return List<PageFileEntry>
     */
    public Map<BillId, PageFileEntry> getPageFileEntries(DaybreakBillId daybreakBillId) throws DataAccessException;

    /**
     * Retrieves all pagefile entries for a single report
     * @param reportDate
     * @return
     */
    public Map<BaseBillId, Map<BillId, PageFileEntry>> getAllPageFileEntries(LocalDate reportDate) throws DataAccessException;

    /**
     * Retrieves a Daybreak Bill corresponding to the given daybreak bill id
     * @param daybreakBillId
     * @return
     */
    public DaybreakBill getDaybreakBill(DaybreakBillId daybreakBillId) throws DataAccessException;

    /**
     * Gets the daybreak bill from the most recent report
     * @param baseBillId
     * @return
     * @throws DataAccessException
     */
    public DaybreakBill getCurrentDaybreakBill(BaseBillId baseBillId) throws DataAccessException;

    /**
     * Gets the current daybreak bill for the specified bill id from within the given date range
     * @param baseBillId
     * @param dateRange
     * @return
     * @throws DataAccessException
     */
    public DaybreakBill getCurrentDaybreakBill(BaseBillId baseBillId, Range<LocalDate> dateRange) throws DataAccessException;

    /**
     * Gets the current daybreak bill from before the reference date for the specified base bill
     * @param baseBillId
     * @param referenceDate
     * @return
     */
    public DaybreakBill getDaybreakBillAtDate(BaseBillId baseBillId, LocalDate referenceDate);

    /**
     * Retrieves all Daybreak Bills from the daybreak report on the given date
     * @param reportDate
     * @return
     * @throws DataAccessException
     */
    public List<DaybreakBill> getDaybreakBills(LocalDate reportDate) throws DataAccessException;

    /**
     * Retrieves all Daybreak Bills from the most recent daybreak report
     * @return
     * @throws DataAccessException
     */
    public List<DaybreakBill> getCurrentDaybreakBills() throws DataAccessException;

    /**
     * Gets all daybreak bills for the latest report date within the specified range
     * @param dateRange
     * @return
     */
    public List<DaybreakBill> getCurrentDaybreakBills(Range<LocalDate> dateRange);

    /**
     * Retrieves the date of the most recent report
     * @return
     * @throws DataAccessException
     */
    public LocalDate getCurrentReportDate() throws DataAccessException;

    /**
     * Returns the latest report date that is before or matching the reference date
     * @return
     */
    public LocalDate getCurrentReportDate(Range<LocalDate> dateRange) throws DataAccessException;

    /**
     * Returns true if the given report date has been used in a spotcheck
     *
     * @param reportDate
     * @return
     * @throws DataAccessException
     */
    public boolean isChecked(LocalDate reportDate) throws DataAccessException;

    /**
     * Returns the date of all existing reports
     * @return
     */
    public List<LocalDate> getAllReportDates() throws DataAccessException;

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
     * Sets the daybreak fragment designated by the given daybreak fragment id as pending processing
     * @param daybreakBillId
     */
    public void setPendingProcessing(DaybreakBillId daybreakBillId);

    /**
     * Sets the daybreak fragments designated by the given report date as pending processing
     * @param reportDate
     */
    public void setPendingProcessing(LocalDate reportDate);

    /**
     * Sets the daybreak fragment designated by the given daybreak fragment id as processed
     * @param daybreakBillId
     */
    public void setProcessed(DaybreakBillId daybreakBillId);

    /**
     * Labels the report designaterd by the given report date as processed, sets all fragments as well
     * @param reportDate
     */
    public void setProcessed(LocalDate reportDate);

    /**
     * Updates or inserts the given PageFileEntry
     * @param pageFileEntry
     */
    public void updatePageFileEntry(PageFileEntry pageFileEntry);

    /**
     * Updates or inserts the given daybreak bill
     * @param daybreakBill
     */
    public void updateDaybreakBill(DaybreakBill daybreakBill);

    /**
     * Updates or inserts a new daybreak report entry.
     * @param reportDate
     */
    public void updateDaybreakReport(LocalDate reportDate);

    /**
     * Sets the status of the checked flag for the given daybreak report
     * @param reportDate
     * @param checked
     */
    public void updateDaybreakReportSetChecked(LocalDate reportDate, boolean checked);
}
