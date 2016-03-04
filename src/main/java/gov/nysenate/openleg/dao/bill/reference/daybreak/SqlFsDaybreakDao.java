package gov.nysenate.openleg.dao.bill.reference.daybreak;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAction;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.*;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.FileIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.util.DateUtils.toDate;

/**
 * Implements a daybreak dao by retieving incoming files from the local filesystem
 *      and storing the resulting files, fragments, and entries in a postgresql database
 */
@Repository
public class SqlFsDaybreakDao extends SqlBaseDao implements DaybreakDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsDaybreakDao.class);

    /** Directory where new daybreak files come in from external sources. */
    private File incomingDaybreakDir;

    /** Directory where daybreak files that have been processed are stored. */
    private File archiveDaybreakDir;

    @PostConstruct
    protected void init() {
        this.incomingDaybreakDir = new File(environment.getStagingDir(), "daybreak");
        this.archiveDaybreakDir = new File(environment.getArchiveDir(), "daybreak");
    }

    /** --- Interfaced Methods --- */

    /** {@inheritDoc } */
    @Override
    public DaybreakFile getDaybreakFile(LocalDate reportDate, String fileName) throws DataAccessException {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        params.addValue("fileName", fileName);

        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FILE_BY_FILENAME.getSql(schema()),
                                        params, new DaybreakFileRowMapper());
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakFile getDaybreakFile(LocalDate reportDate, DaybreakDocType fileType) throws DataAccessException {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        params.addValue("fileType", fileType.toString().toLowerCase());

        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FILE_BY_TYPE.getSql(schema()),
                                            params, new DaybreakFileRowMapper());
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakReport<DaybreakFile> getDaybreakReport(LocalDate reportDate) throws DataAccessException {
        // Get all files for the given report date
        MapSqlParameterSource params = getReportDateParams(reportDate);
        List<DaybreakFile> daybreakFiles = jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_FILES_FROM_REPORT.getSql(schema()),
                params, new DaybreakFileRowMapper());

        // Attempt to create a daybreak report from those files
        try{
            return new DaybreakReport<>(daybreakFiles);
        }
        catch (DaybreakReport.DaybreakReportInsertException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakReportSet<DaybreakFile> getIncomingReports() throws IOException {
        logger.debug("Getting incoming files from " + incomingDaybreakDir.getAbsolutePath());
        // Get all report files from the incoming directory
        List<File> incomingFiles = new ArrayList<>(FileIOUtils.safeListFiles(this.incomingDaybreakDir, null, false));

        DaybreakReportSet<DaybreakFile> reportSet = new DaybreakReportSet<>();
        // Group the files into reports
        for(File file : incomingFiles){
            if(DaybreakDocType.getFileDocType(file.getName()) != null) {
                try {
                    DaybreakFile daybreakFile = new DaybreakFile(file);
                    reportSet.insertDaybreakDocument(daybreakFile);
                } catch (IllegalArgumentException ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
        return reportSet;
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakFragment getDaybreakFragment(DaybreakBillId daybreakBillId) throws DataAccessException {
        // Get the main fragment data
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        DaybreakFragment daybreakFragment = jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FRAGMENT.getSql(schema()),
                params, new DaybreakFragmentRowMapper());
        // Add page file entries
        daybreakFragment.setPageFileEntries(getPageFileEntries(daybreakBillId));

        return daybreakFragment;
    }

    /** {@inheritDoc } */
    @Override
    public List<DaybreakFragment> getDaybreakFragments(LocalDate reportDate) throws DataAccessException {
        // Get a list of all fragments for the report date
        MapSqlParameterSource params = getReportDateParams(reportDate);
        List<DaybreakFragment> daybreakFragments = jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_FRAGMENTS_BY_REPORT_DATE.getSql(schema()),
                params, new DaybreakFragmentRowMapper());
        // Get all page file entries for the report date mapped to a base bill id
        Map<BaseBillId, Map<BillId, PageFileEntry>> pageFileEntries = getAllPageFileEntries(reportDate);
        // Add the corresponding page file entries to each fragment
        for(DaybreakFragment daybreakFragment : daybreakFragments){
            daybreakFragment.setPageFileEntries(pageFileEntries.get(BaseBillId.getBaseId(daybreakFragment.getBillId())));
        }
        return daybreakFragments;
    }

    /** {@inheritDoc } */
    @Override
    public List<DaybreakFragment> getPendingDaybreakFragments() {
        try {
            // Get a list of all pending daybreak fragments
            List<DaybreakFragment> pendingFragments = jdbcNamed.query(
                    SqlDaybreakQuery.SELECT_PENDING_DAYBREAK_FRAGMENTS.getSql(schema()), new DaybreakFragmentRowMapper());
            // Group The fragments in a map by their report date
            Map<LocalDate, List<DaybreakFragment>> pendingReportFragments = new HashMap<>();
            for(DaybreakFragment daybreakFragment : pendingFragments){
                LocalDate reportDate = daybreakFragment.getReportDate();
                if(!pendingReportFragments.containsKey(reportDate)){
                    pendingReportFragments.put(reportDate, new ArrayList<>());
                }
                pendingReportFragments.get(reportDate).add(daybreakFragment);
            }
            // Get page file entries for each represented report date and apply to each fragment for the report date
            for(LocalDate reportDate : pendingReportFragments.keySet()){
                Map<BaseBillId, Map<BillId, PageFileEntry>> pageFileEntries = getAllPageFileEntries(reportDate);
                for(DaybreakFragment daybreakFragment : pendingReportFragments.get(reportDate)){
                    daybreakFragment.setPageFileEntries(pageFileEntries.get(BaseBillId.getBaseId(daybreakFragment.getBillId())));
                }
            }

            return pendingFragments;
        }
        catch(EmptyResultDataAccessException ex){
            // Return an empty list if no pending fragments are found
            return new ArrayList<>();
        }
    }

    /** {@inheritDoc } */
    @Override
    public Map<BillId, PageFileEntry> getPageFileEntries(DaybreakBillId daybreakBillId) throws DataAccessException {
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        List<PageFileEntry> pageFileEntries = jdbcNamed.query(
                SqlDaybreakQuery.SELECT_PAGE_FILE_ENTRIES_BY_BILL.getSql(schema()), params, new PageFileEntryRowMapper());
        return Maps.uniqueIndex(pageFileEntries, daybreakBillId.getBaseBillId().getChamber() == Chamber.SENATE ?
                PageFileEntry::getSenateBillId : PageFileEntry::getAssemblyBillId);
    }

    /** {@inheritDoc } */
    @Override
    public Map<BaseBillId, Map<BillId, PageFileEntry>> getAllPageFileEntries(LocalDate reportDate) throws DataAccessException {
        // Get a list of all page file entries
        MapSqlParameterSource params = getReportDateParams(reportDate);
        List<PageFileEntry> pageFileEntries = jdbcNamed.query(
            SqlDaybreakQuery.SELECT_PAGE_FILE_ENTRIES_BY_REPORT.getSql(schema()), params, new PageFileEntryRowMapper());

        // Map entries by base print number, then amendment
        Map<BaseBillId, Map<BillId, PageFileEntry>> reportPageFileEntries = new HashMap<>();
        for(PageFileEntry pageFileEntry : pageFileEntries){
            // Each entry can be used twice, for senate or assembly
            for(BillId billId : pageFileEntry.getBillIds()){
                if(!reportPageFileEntries.containsKey(BillId.getBaseId(billId))){
                    reportPageFileEntries.put(BillId.getBaseId(billId), new HashMap<>());
                }
                reportPageFileEntries.get(BillId.getBaseId(billId)).put(billId, pageFileEntry);
            }
        }
        return reportPageFileEntries;
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakBill getDaybreakBill(DaybreakBillId daybreakBillId)  throws DataAccessException{
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        DaybreakBill daybreakBill = jdbcNamed.queryForObject(
                                        SqlDaybreakQuery.SELECT_DAYBREAK_BILL.getSql(schema()),
                                        params, new DaybreakBillRowMapper());
        addAdditionalFields(daybreakBill);
        return daybreakBill;
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakBill getCurrentDaybreakBill(BaseBillId baseBillId) throws DataAccessException {
        return getDaybreakBillAtDate(baseBillId, LocalDate.now());
    }

    @Override
    public DaybreakBill getCurrentDaybreakBill(BaseBillId baseBillId, Range<LocalDate> dateRange) throws DataAccessException {
        return getDaybreakBillAtDate(baseBillId, getCurrentReportDate(dateRange));
    }

    /** {@inheritDoc } */
    @Override
    public DaybreakBill getDaybreakBillAtDate(BaseBillId baseBillId, LocalDate referenceDate) {
        return getDaybreakBill(new DaybreakBillId(baseBillId, getCurrentReportDate()));
    }

    /** {@inheritDoc } */
    @Override
    public List<DaybreakBill> getDaybreakBills(LocalDate reportDate)  throws DataAccessException{
        MapSqlParameterSource params = getReportDateParams(reportDate);
        List<DaybreakBill> daybreakBills = jdbcNamed.query(
                                                SqlDaybreakQuery.SELECT_DAYBREAK_BILL_BY_REPORT.getSql(schema()),
                                                params, new DaybreakBillRowMapper());
        daybreakBills.parallelStream().forEach(this::addAdditionalFields);
        return daybreakBills;
    }

    /** {@inheritDoc } */
    @Override
    public List<DaybreakBill> getCurrentDaybreakBills() throws DataAccessException {
        return getDaybreakBills(getCurrentReportDate());
    }

    /** {@inheritDoc } */
    @Override
    public List<DaybreakBill> getCurrentDaybreakBills(Range<LocalDate> dateRange) {
        return getDaybreakBills(getCurrentReportDate(dateRange));
    }

    /** {@inheritDoc } */
    @Override
    public LocalDate getCurrentReportDate() throws DataAccessException {
        return getCurrentReportDate(Range.closed(DateUtils.LONG_AGO, LocalDate.now()));
    }

    /** {@inheritDoc }  */
    @Override
    public LocalDate getCurrentReportDate(Range<LocalDate> dateRange) throws DataAccessException {
        MapSqlParameterSource params = getReportDateRangeParams(dateRange);
        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_REPORTS.getSql(
                        schema(),
                        new OrderBy("report_date", SortOrder.DESC),
                        new LimitOffset(1)),
                params,
                new DaybreakReportDateRowMapper()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChecked(LocalDate reportDate) throws DataAccessException {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_REPORT_CHECKED.getSql(schema()), params, Boolean.class);
    }

    @Override
    public List<LocalDate> getAllReportDates() throws DataAccessException {
    Range<LocalDate> allDates = Range.closed(DateUtils.LONG_AGO, LocalDate.now());
        MapSqlParameterSource params = getReportDateRangeParams(allDates);
        return jdbcNamed.query(SqlDaybreakQuery.SELECT_REPORTS.getSql(schema()), params, new DaybreakReportDateRowMapper());
    }

    /** {@inheritDoc } */
    @Override
    public void archiveDaybreakFile(DaybreakFile daybreakFile) throws IOException {
        File archivedFile = new File(this.archiveDaybreakDir, daybreakFile.getFile().getName());
        moveFile(daybreakFile.getFile(), archivedFile);
        MapSqlParameterSource params = getDaybreakFileParams(daybreakFile);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FILE_ARCHIVED.getSql(schema()), params);
        daybreakFile.setArchived(true);
        daybreakFile.setFile(archivedFile);
    }

    /** {@inheritDoc } */
    @Override
    public void updateDaybreakFile(DaybreakFile daybreakFile) {
        MapSqlParameterSource params = getDaybreakFileParams(daybreakFile);
        if(jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FILE.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_FILE.getSql(schema()), params);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void updateDaybreakFragment(DaybreakFragment daybreakFragment) {
        MapSqlParameterSource params = getDaybreakFragmentParams(daybreakFragment);
        if(jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FRAGMENT.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_FRAGMENT.getSql(schema()), params);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void setPendingProcessing(DaybreakBillId daybreakBillId) {
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FRAGMENT_PENDING_PROCESSING.getSql(schema()), params);
    }

    /** {@inheritDoc } */
    @Override
    public void setPendingProcessing(LocalDate reportDate) {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FRAGMENT_PENDING_PROCESSING_REPORT.getSql(schema()), params);
        // Set the report as unprocessed
        updateDaybreakReport(reportDate);
    }

    /** {@inheritDoc } */
    @Override
    public void setProcessed(DaybreakBillId daybreakBillId) {
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FRAGMENT_PROCESSED.getSql(schema()), params);
    }

    /** {@inheritDoc } */
    @Override
    public void setProcessed(LocalDate reportDate) {
        MapSqlParameterSource params = getDaybreakReportParams(reportDate, true, false);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_REPORT.getSql(schema()), params);
        // Set all fragments as processed too
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_FRAGMENT_PROCESSED_REPORT.getSql(schema()), params);
    }

    /** {@inheritDoc } */
    @Override
    public void updatePageFileEntry(PageFileEntry pageFileEntry) {
        MapSqlParameterSource params = getPageFileEntryParams(pageFileEntry);
        if(jdbcNamed.update(SqlDaybreakQuery.UPDATE_PAGE_FILE_ENTRY.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlDaybreakQuery.INSERT_PAGE_FILE_ENTRY.getSql(schema()), params);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void updateDaybreakBill(DaybreakBill daybreakBill) {
        // Update the bill table
        MapSqlParameterSource params = getDaybreakBillParams(daybreakBill);
        if(jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_BILL.getSql(schema()), params) ==0){
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_BILL.getSql(schema()), params);
        }
        // Update the bill's associated tables
        updateDaybreakBillActions(daybreakBill.getDaybreakBillId(), daybreakBill.getActions());
        updateDaybreakBillAmendments(daybreakBill.getDaybreakBillId(), daybreakBill.getAmendments());
        updateDaybreakBillCoSponsors(daybreakBill.getDaybreakBillId(), daybreakBill.getCosponsors());
        updateDaybreakBillMultiSponsors(daybreakBill.getDaybreakBillId(), daybreakBill.getMultiSponsors());
    }

    /** {@inheritDoc } */
    @Override
    public void updateDaybreakReport(LocalDate reportDate) {
        MapSqlParameterSource params = getDaybreakReportParams(reportDate, false, false);
        if(jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_REPORT.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_REPORT.getSql(schema()), params);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void updateDaybreakReportSetChecked(LocalDate reportDate, boolean checked) {
        MapSqlParameterSource params = getDaybreakReportParams(reportDate, true, checked);
        jdbcNamed.update(SqlDaybreakQuery.UPDATE_DAYBREAK_REPORT.getSql(schema()), params);
    }

    /** --- Internal Methods --- */

    /**
     * Retrieves and sets all daybreak bill fields that are not in the primary daybreak bill table
     * @param daybreakBill
     */
    private void addAdditionalFields(DaybreakBill daybreakBill){
        daybreakBill.setActions(getDaybreakBillActions(daybreakBill.getDaybreakBillId()));
        daybreakBill.setAmendments(getDaybreakBillAmendments(daybreakBill.getDaybreakBillId()));
        daybreakBill.setCosponsors(getDaybreakBillCoSponsors(daybreakBill.getDaybreakBillId()));
        daybreakBill.setMultiSponsors(getDaybreakBillMultiSponsors(daybreakBill.getDaybreakBillId()));
    }

    /**
     * Gets all bill amendments for the specified daybreak bill
     * @param daybreakBillId
     * @return Map<Version, DaybreakBillAmendment>
     */
    private Map<Version, DaybreakBillAmendment> getDaybreakBillAmendments(DaybreakBillId daybreakBillId){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        List<DaybreakBillAmendment> amendments = jdbcNamed.query(
                                                    SqlDaybreakQuery.SELECT_DAYBREAK_BILL_AMENDMENTS.getSql(schema()),
                                                    params, new DaybreakBillAmendmentRowMapper());
        return Maps.uniqueIndex(amendments, amendment -> amendment.getBillId().getVersion());
    }

    /**
     * Gets all actions for the specified daybreak bill sorted by sequence number
     * @param daybreakBillId
     * @return List<BillAction>
     */
    private List<BillAction> getDaybreakBillActions(DaybreakBillId daybreakBillId){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        return jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_BILL_ACTIONS.getSql(
                        schema(), new OrderBy("sequence_no", SortOrder.ASC), null),
                params, new DaybreakBillActionRowMapper());
    }

    /**
     * Gets all cosponsors for the specified daybreak bill
     * @param daybreakBillId
     * @return
     */
    private List<String> getDaybreakBillCoSponsors(DaybreakBillId daybreakBillId) {
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        return jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_BILL_COSPONSORS.getSql(schema()),
                params, new DaybreakSponsorRowMapper());
    }

    /**
     * Gets all multisponsors for the specified daybreak bill
     * @param daybreakBillId
     * @return
     */
    private List<String> getDaybreakBillMultiSponsors(DaybreakBillId daybreakBillId) {
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        return jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_BILL_MULTISPONSORS.getSql(schema()),
                params, new DaybreakSponsorRowMapper());
    }

    /**
     * Sets all given amendments for the given daybreak bill id
     * @param daybreakBillId
     * @param amendments
     */
    private void updateDaybreakBillAmendments(DaybreakBillId daybreakBillId,
                                              Map<Version, DaybreakBillAmendment> amendments){
        // Delete existing amendments
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.DELETE_DAYBREAK_BILL_AMENDMENTS.getSql(schema()), params);
        // Insert new amendments
        for(DaybreakBillAmendment amendment : amendments.values()){
            params = getDaybreakBillAmendmentParams(daybreakBillId, amendment);
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_BILL_AMENDMENT.getSql(schema()), params);
        }
    }

    /**
     * Sets all given actions for the given daybreak bill id
     * @param daybreakBillId
     * @param billActions
     */
    private void updateDaybreakBillActions(DaybreakBillId daybreakBillId, List<BillAction> billActions){
        // Delete existing amendments
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.DELETE_DAYBREAK_BILL_ACTIONS.getSql(schema()), params);
        // Insert new amendments
        for(BillAction billAction : billActions){
            params = getDaybreakBillActionParams(daybreakBillId, billAction);
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_BILL_ACTION.getSql(schema()), params);
        }
    }

    /**
     * Sets all given cosponsors for the given daybreak bill id
     * @param daybreakBillId
     * @param coSponsors
     */
    private void updateDaybreakBillCoSponsors(DaybreakBillId daybreakBillId, List<String> coSponsors){
        // Delete existing cosponsors
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.DELETE_DAYBREAK_BILL_COSPONSORS.getSql(schema()), params);
        // Insert new cosponsors
        for(String sponsor : coSponsors){
            params = getDaybreakBillSponsorParams(daybreakBillId, sponsor);
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_BILL_COSPONSOR.getSql(schema()), params);
        }
    }

    /**
     * Sets all given multi-sponsors for the given daybreak bill id
     * @param daybreakBillId
     * @param multiSponsors
     */
    private void updateDaybreakBillMultiSponsors(DaybreakBillId daybreakBillId, List<String> multiSponsors){
        // Delete existing multi-sponsors
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        jdbcNamed.update(SqlDaybreakQuery.DELETE_DAYBREAK_BILL_MULTISPONSORS.getSql(schema()), params);
        // Insert new multi-sponsors
        for(String sponsor : multiSponsors){
            params = getDaybreakBillSponsorParams(daybreakBillId, sponsor);
            jdbcNamed.update(SqlDaybreakQuery.INSERT_DAYBREAK_BILL_MULTISPONSOR.getSql(schema()), params);
        }
    }

    /**
     * Get file handle from incoming daybreak directory.
     */
    private File getFileInIncomingDir(String fileName) {
        return new File(this.incomingDaybreakDir, fileName);
    }

    /**
     * Get file handle from the daybreak archive directory.
     */
    private File getFileInArchiveDir(String fileName) {
        return new File(this.archiveDaybreakDir, fileName);
    }

    /**
     * Get a Daybreak file from archive or incoming directory
     */
    private DaybreakFile findDaybreakFile(String fileName){
        try {
            try {
                return new DaybreakFile(getFileInArchiveDir(fileName));
            } catch (FileNotFoundException ex) {
                try {
                    return new DaybreakFile((getFileInIncomingDir(fileName)));
                } catch (FileNotFoundException ex2) {
                    logger.error("Could not find daybreak file " + fileName + " in incoming or archive directories");
                    return null;
                }
            }
        }
        catch(IllegalArgumentException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    /** --- Row Mappers --- */

    private class DaybreakFileRowMapper implements RowMapper<DaybreakFile>{

        @Override
        public DaybreakFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            LocalDateTime stagedDateTime = getLocalDateTimeFromRs(rs, "staged_date_time");
            boolean archived = rs.getBoolean("is_archived");

            File file = archived ? getFileInArchiveDir(fileName) : getFileInIncomingDir(fileName);
            try {
                return new DaybreakFile(file, stagedDateTime, archived);
            }
            catch ( IOException | IllegalArgumentException ex ){
                logger.error(ex.getMessage());
                return null;
            }
        }
    }

    private class DaybreakFragmentRowMapper implements RowMapper<DaybreakFragment>{
        @Override
        public DaybreakFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillId billId = new BillId(rs.getString("bill_print_no") + rs.getString("bill_active_version"),
                    new SessionYear(rs.getInt("bill_session_year")));
            DaybreakFile daybreakFile = findDaybreakFile(rs.getString("filename"));
            String fragmentText = rs.getString("fragment_text");

            return new DaybreakFragment(billId, daybreakFile, fragmentText);
        }
    }

    private class PageFileEntryRowMapper implements RowMapper<PageFileEntry>{
        @Override
        public PageFileEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            SessionYear billSessionYear = new SessionYear(rs.getInt("bill_session_year"));
            //Attempt to get a senate bill id
            String senatePrintNo = rs.getString("senate_bill_print_no");
            BillId senateBillId = null;
            if(senatePrintNo!=null){
                Version senateBillVersion = Version.of(rs.getString("senate_bill_version"));
                senateBillId = new BillId(senatePrintNo, billSessionYear, senateBillVersion);
            }
            //Attempt to get an assembly bill id
            String assemblyPrintNo = rs.getString("assembly_bill_print_no");
            BillId assemblyBillId = null;
            if(assemblyPrintNo!=null){
                Version assemblyBillVersion = Version.of(rs.getString("assembly_bill_version"));
                assemblyBillId = new BillId(assemblyPrintNo, billSessionYear, assemblyBillVersion);
            }

            LocalDate billPublishDate = getLocalDateFromRs(rs, "bill_publish_date");
            int pageCount = rs.getInt("page_count");

            DaybreakFile pageFile = findDaybreakFile(rs.getString("filename"));

            return new PageFileEntry(senateBillId, assemblyBillId, pageFile, billPublishDate, pageCount);
        }
    }

    private class DaybreakBillRowMapper implements RowMapper<DaybreakBill>{
        @Override
        public DaybreakBill mapRow(ResultSet rs, int rowNum) throws SQLException {
            DaybreakBill daybreakBill = new DaybreakBill();
            daybreakBill.setReportDate(getLocalDateFromRs(rs, "report_date"));
            daybreakBill.setBaseBillId(new BaseBillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
            daybreakBill.setActiveVersion(Version.of(rs.getString("active_version")));
            daybreakBill.setTitle(rs.getString("title"));
            daybreakBill.setSponsor( rs.getString("sponsor"));
            daybreakBill.setLawCodeAndSummary(rs.getString("summary"));
            daybreakBill.setLawSection(rs.getString("law_section"));
            return daybreakBill;
        }
    }

    private class DaybreakBillActionRowMapper implements RowMapper<BillAction>{
        @Override
        public BillAction mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillAction billAction = new BillAction();
            billAction.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
            billAction.setDate(getLocalDateFromRs(rs, "action_date"));
            billAction.setChamber(Chamber.getValue(rs.getString("chamber")));
            billAction.setText(rs.getString("text"));
            billAction.setSequenceNo(rs.getInt("sequence_no"));
            return billAction;
        }
    }

    private class DaybreakBillAmendmentRowMapper implements RowMapper<DaybreakBillAmendment>{
        @Override
        public DaybreakBillAmendment mapRow(ResultSet rs, int rowNum) throws SQLException {
            DaybreakBillAmendment daybreakBillAmendment = new DaybreakBillAmendment();
            daybreakBillAmendment.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                    rs.getString("version")));
            if(rs.getString("same_as")!=null) {
                daybreakBillAmendment.setSameAs(new BillId(rs.getString("same_as"), rs.getInt("bill_session_year")));
            }
            daybreakBillAmendment.setPageCount(rs.getInt("page_count"));
            daybreakBillAmendment.setPublishDate(getLocalDateFromRs(rs, "publish_date"));
            return daybreakBillAmendment;
        }
    }

    private class DaybreakSponsorRowMapper implements RowMapper<String>{
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("member_short_name");
        }
    }

    private class DaybreakReportDateRowMapper implements RowMapper<LocalDate>{
        @Override
        public LocalDate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return getLocalDateFromRs(rs, "report_date");
        }
    }

    /** --- Param Mappers --- */

    private MapSqlParameterSource getReportDateParams(LocalDate reportDate){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reportDate", toDate(reportDate));
        return params;
    }

    private MapSqlParameterSource getReportDateRangeParams(Range<LocalDate> dateRange){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rangeStart", toDate(dateRange.lowerEndpoint()));
        params.addValue("rangeEnd", toDate(dateRange.upperEndpoint()));
        return params;
    }

    private MapSqlParameterSource getDaybreakBillIdParams(DaybreakBillId daybreakBillId){
        MapSqlParameterSource params = getReportDateParams(daybreakBillId.getReportDate());
        params.addValue("billPrintNo", daybreakBillId.getBaseBillId().getBasePrintNo());
        params.addValue("billSessionYear", daybreakBillId.getBaseBillId().getSession().getYear());
        return params;
    }

    private MapSqlParameterSource getDaybreakFileParams(DaybreakFile daybreakFile){
        MapSqlParameterSource params = getReportDateParams(daybreakFile.getReportDate());
        params.addValue("fileName", daybreakFile.getFileName());
        params.addValue("fileType", daybreakFile.getDaybreakDocType().toString().toLowerCase());
        params.addValue("isArchived", daybreakFile.isArchived());
        return params;
    }

    private MapSqlParameterSource getDaybreakFragmentParams(DaybreakFragment daybreakFragment){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakFragment.getDaybreakBillId());
        params.addValue("billActiveVersion", daybreakFragment.getBillId().getVersion().getValue());
        params.addValue("fileName", daybreakFragment.getDaybreakFile().getFileName());
        params.addValue("fragmentText", daybreakFragment.getDaybreakText());
        return params;
    }

    private MapSqlParameterSource getPageFileEntryParams(PageFileEntry pageFileEntry){
        MapSqlParameterSource params = getReportDateParams(pageFileEntry.getReportDate());
        params.addValue("fileName", pageFileEntry.getDaybreakFile().getFileName());
        params.addValue("billSessionYear", pageFileEntry.getBillIds().get(0).getSession().getYear());
        params.addValue("billPrintNo", pageFileEntry.getBillIds().get(0).getBasePrintNo());
        params.addValue("billPublishDate", toDate(pageFileEntry.getPublishedDate()));
        params.addValue("pageCount", pageFileEntry.getPageCount());
        params.addValue("senateBillPrintNo", pageFileEntry.getSenateBillId()!=null ?
                                                pageFileEntry.getSenateBillId().getBasePrintNo() : null);
        params.addValue("senateBillVersion",  pageFileEntry.getSenateBillId()!=null ?
                                                pageFileEntry.getSenateBillId().getVersion().getValue() : null);
        params.addValue("assemblyBillPrintNo", pageFileEntry.getAssemblyBillId()!=null ?
                                                pageFileEntry.getAssemblyBillId().getBasePrintNo() : null);
        params.addValue("assemblyBillVersion", pageFileEntry.getAssemblyBillId()!=null ?
                                                pageFileEntry.getAssemblyBillId().getVersion().getValue() : null);
        return params;
    }

    private MapSqlParameterSource getDaybreakBillParams(DaybreakBill daybreakBill){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBill.getDaybreakBillId());
        params.addValue("activeVersion", daybreakBill.getActiveVersion().getValue());
        params.addValue("title", daybreakBill.getTitle());
        params.addValue("sponsor", daybreakBill.getSponsor());
        params.addValue("lawAndSummary", daybreakBill.getLawCodeAndSummary());
        params.addValue("lawSection", daybreakBill.getLawSection());
        return params;
    }

    private MapSqlParameterSource getDaybreakBillActionParams(DaybreakBillId daybreakBillId, BillAction action){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        params.addValue("actionDate", toDate(action.getDate()));
        params.addValue("chamber", action.getChamber().asSqlEnum());
        params.addValue("text", action.getText());
        params.addValue("sequenceNo", action.getSequenceNo());
        return params;
    }

    private MapSqlParameterSource getDaybreakBillAmendmentParams(DaybreakBillId daybreakBillId,
                                                                 DaybreakBillAmendment daybreakBillAmendment){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        params.addValue("version", daybreakBillAmendment.getBillId().getVersion().getValue());
        params.addValue("sameAs", daybreakBillAmendment.getSameAs() != null ?
                                    daybreakBillAmendment.getSameAs().getPrintNo() :
                                    null );
        params.addValue("publishDate", toDate(daybreakBillAmendment.getPublishDate()));
        params.addValue("pageCount", daybreakBillAmendment.getPageCount());
        return params;
    }

    private MapSqlParameterSource getDaybreakBillSponsorParams(DaybreakBillId daybreakBillId, String sponsorShortName){
        MapSqlParameterSource params = getDaybreakBillIdParams(daybreakBillId);
        params.addValue("memberShortName", sponsorShortName);
        return params;
    }

    private MapSqlParameterSource getDaybreakReportParams(LocalDate reportDate, boolean processed, boolean checked){
        MapSqlParameterSource params = getReportDateParams(reportDate);
        params.addValue("processed", processed);
        params.addValue("checked", checked);
        return params;
    }
}
