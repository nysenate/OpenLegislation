package gov.nysenate.openleg.spotchecks.daybreak.bill;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.spotchecks.daybreak.DaybreakFile;
import gov.nysenate.openleg.spotchecks.daybreak.DaybreakFragment;
import gov.nysenate.openleg.spotchecks.daybreak.DaybreakReportSet;
import gov.nysenate.openleg.spotchecks.daybreak.PageFileEntry;
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
     * Gets all DaybreakFiles from the incoming directory
     *
     * @throws java.io.IOException
     * @return List<DaybreakFile>
     */
    DaybreakReportSet<DaybreakFile> getIncomingReports() throws IOException;

    /**
     * Retrieves all DaybreakFragments that have not yet been processed
     *
     * @return List<DaybreakFragment>
     */
    List<DaybreakFragment> getPendingDaybreakFragments();

    /**
     * Retrieves all pagefile entries for a single report
     * @param reportDate
     * @return
     */
    Map<BaseBillId, Map<BillId, PageFileEntry>> getAllPageFileEntries(LocalDate reportDate) throws DataAccessException;

    /**
     * Retrieves a Daybreak Bill corresponding to the given daybreak bill id
     * @param daybreakBillId
     * @return
     */
    DaybreakBill getDaybreakBill(DaybreakBillId daybreakBillId) throws DataAccessException;

    /**
     * Gets the current daybreak bill from before the reference date for the specified base bill
     * @param baseBillId
     * @param referenceDate
     * @return
     */
    DaybreakBill getDaybreakBillAtDate(BaseBillId baseBillId, LocalDate referenceDate);

    /**
     * Retrieves all Daybreak Bills from the daybreak report on the given date
     * @param reportDate
     * @return
     * @throws DataAccessException
     */
    List<DaybreakBill> getDaybreakBills(LocalDate reportDate) throws DataAccessException;

    /**
     * Retrieves all Daybreak Bills from the most recent daybreak report
     * @return
     * @throws DataAccessException
     */
    List<DaybreakBill> getCurrentDaybreakBills() throws DataAccessException;

    /**
     * Gets all daybreak bills for the latest report date within the specified range
     * @param dateRange
     * @return
     */
    List<DaybreakBill> getCurrentDaybreakBills(Range<LocalDate> dateRange);

    /**
     * Retrieves the date of the most recent report
     * @return
     * @throws DataAccessException
     */
    LocalDate getCurrentReportDate() throws DataAccessException;

    /**
     * Returns the latest report date that is before or matching the reference date
     * @return
     */
    LocalDate getCurrentReportDate(Range<LocalDate> dateRange) throws DataAccessException;

    /**
     * Returns true if the given report date has been used in a spotcheck
     *
     * @param reportDate
     * @return
     * @throws DataAccessException
     */
    boolean isChecked(LocalDate reportDate) throws DataAccessException;

    /**
     * Returns the date of all existing reports
     * @return
     */
    List<LocalDate> getAllReportDates() throws DataAccessException;

    /** --- Update/Insert Methods --- */

    /**
     * Moves the file pointed at by the given Daybreak File to the archived directory
     *  so that getIncomingReports will no longer return this DaybreakFile
     * @param daybreakFile
     * @throws IOException
     */
    void archiveDaybreakFile(DaybreakFile daybreakFile) throws IOException;

    /**
     * Updates or inserts the given DaybreakFile
     * @param daybreakFile
     */
    void updateDaybreakFile(DaybreakFile daybreakFile);

    /**
     * Updates or inserts the given DaybreakFragment
     * @param daybreakFragment
     */
    void updateDaybreakFragment(DaybreakFragment daybreakFragment);

    /**
     * Sets the daybreak fragment designated by the given daybreak fragment id as pending processing
     * @param daybreakBillId
     */
    void setPendingProcessing(DaybreakBillId daybreakBillId);

    /**
     * Sets the daybreak fragments designated by the given report date as pending processing
     * @param reportDate
     */
    void setPendingProcessing(LocalDate reportDate);

    /**
     * Sets the daybreak fragment designated by the given daybreak fragment id as processed
     * @param daybreakBillId
     */
    void setProcessed(DaybreakBillId daybreakBillId);

    /**
     * Labels the report designaterd by the given report date as processed, sets all fragments as well
     * @param reportDate
     */
    void setProcessed(LocalDate reportDate);

    /**
     * Updates or inserts the given PageFileEntry
     * @param pageFileEntry
     */
    void updatePageFileEntry(PageFileEntry pageFileEntry);

    /**
     * Updates or inserts the given daybreak bill
     * @param daybreakBill
     */
    void updateDaybreakBill(DaybreakBill daybreakBill);

    /**
     * Updates or inserts a new daybreak report entry.
     * @param reportDate
     */
    void updateDaybreakReport(LocalDate reportDate);

    /**
     * Sets the status of the checked flag for the given daybreak report
     * @param reportDate
     * @param checked
     */
    void updateDaybreakReportSetChecked(LocalDate reportDate, boolean checked);
}
