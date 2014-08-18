package gov.nysenate.openleg.dao.daybreak;

import com.sun.javaws.exceptions.InvalidArgumentException;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.daybreak.*;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        this.incomingDaybreakDir = new File(environment.getStagingDirectory(), "daybreak");
        this.archiveDaybreakDir = new File(environment.getArchiveDirectory(), "daybreak");
    }

    /** --- Interfaced Methods --- */

    /** {@InheritDoc } */
    @Override
    public DaybreakFile getDaybreakFile(LocalDate reportDate, String fileName) {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        params.addValue("fileName", fileName);

        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FILE_BY_FILENAME.getSql(schema()),
                                        params, new DaybreakFileRowMapper());
    }

    /** {@InheritDoc } */
    @Override
    public DaybreakFile getDaybreakFile(LocalDate reportDate, DaybreakDocType fileType) {
        MapSqlParameterSource params = getReportDateParams(reportDate);
        params.addValue("fileType", fileType.toString().toLowerCase());

        return jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FILE_BY_TYPE.getSql(schema()),
                                            params, new DaybreakFileRowMapper());
    }

    /** {@InheritDoc } */
    @Override
    public DaybreakReport<DaybreakFile> getDaybreakReport(LocalDate reportDate) {
        MapSqlParameterSource params = getReportDateParams(reportDate);

        List<DaybreakFile> daybreakFiles = jdbcNamed.query(
                SqlDaybreakQuery.SELECT_DAYBREAK_FILES_FROM_REPORT.getSql(schema()),
                params, new DaybreakFileRowMapper());

        try{
            DaybreakReport<DaybreakFile> daybreakReport = new DaybreakReport<>(daybreakFiles);
            return daybreakReport;
        }
        catch (DaybreakReport.DaybreakReportInsertException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    /** {@InheritDoc } */
    @Override
    public DaybreakReportSet<DaybreakFile> getIncomingReports() throws IOException {
        List<File> incomingFiles = new ArrayList<>(FileIOUtils.safeListFiles(this.incomingDaybreakDir,
                                                                             DaybreakDocType.getFileExts(), false));
        DaybreakReportSet<DaybreakFile> reportSet = new DaybreakReportSet<>();
        for(File file : incomingFiles){
            try{
                DaybreakFile daybreakFile = new DaybreakFile(file);
                reportSet.insertDaybreakDocument(daybreakFile);
            }
            catch(InvalidArgumentException ex){
                logger.error(ex.getMessage());
            }
        }
        return reportSet;
    }

    /** {@InheritDoc } */
    @Override
    public DaybreakFragment getDaybreakFragment(DaybreakFragmentId daybreakFragmentId) {
        DaybreakReport<DaybreakFile> associatedReport = getDaybreakReport(daybreakFragmentId.getReportDate());
        MapSqlParameterSource params = getDaybreakFragmentIdParams(daybreakFragmentId);
        DaybreakFragment daybreakFragment = jdbcNamed.queryForObject(SqlDaybreakQuery.SELECT_DAYBREAK_FRAGMENT.getSql(schema()),
                                                                    params, new DaybreakFragmentRowMapper(associatedReport));
        daybreakFragment.setPageFileEntries(getPageFileEntries(daybreakFragmentId));

        return daybreakFragment;
    }

    /** {@InheritDoc } */
    @Override
    public List<DaybreakFragment> getDaybreakFragments(LocalDate reportDate) {
        DaybreakReport<DaybreakFile> associatedReport = getDaybreakReport(reportDate);
        MapSqlParameterSource params = getReportDateParams(reportDate);
        List<DaybreakFragment> daybreakFragments = jdbcNamed.query(
                                            SqlDaybreakQuery.SELECT_DAYBREAK_FRAGMENTS_BY_REPORT_DATE.getSql(schema()),
                                            params, new DaybreakFragmentRowMapper(associatedReport));
        Map<BaseBillId, Map<BillId, PageFileEntry>> pageFileEntries = getAllPageFileEntries(reportDate);
        for(DaybreakFragment daybreakFragment : daybreakFragments){
            daybreakFragment.setPageFileEntries(pageFileEntries.get(BaseBillId.getBaseId(daybreakFragment.getBillId())));
        }
        return daybreakFragments;
    }

    /** {@InheritDoc } */
    @Override
    public List<DaybreakFragment> getPendingDaybreakFragments() {
        return null;
    }

    /** {@InheritDoc } */
    @Override
    public Map<BillId, PageFileEntry> getPageFileEntries(DaybreakFragmentId daybreakFragmentId) {
        return null;
    }

    /** {@InheritDoc } */
    @Override
    public Map<BaseBillId, Map<BillId, PageFileEntry>> getAllPageFileEntries(LocalDate reportDate){
        return null;
    }

    /** {@InheritDoc } */
    @Override
    public void archiveDaybreakFile(DaybreakFile daybreakFile) throws IOException {

    }

    /** {@InheritDoc } */
    @Override
    public void updateDaybreakFile(DaybreakFile daybreakFile) {

    }

    /** {@InheritDoc } */
    @Override
    public void updateDaybreakFragment(DaybreakFragment daybreakFragment) {

    }

    /** {@InheritDoc } */
    @Override
    public void updatePageFileEntry(PageFileEntry pageFileEntry) {

    }

    /** {@InheritDoc } */
    @Override
    public void deletePageFileEntries(LocalDate reportDate) {

    }

    /** --- Row Mappers --- */

    private class DaybreakFileRowMapper implements RowMapper<DaybreakFile>{

        @Override
        public DaybreakFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            LocalDateTime stagedDateTime = getLocalDateTime(rs, "staged_date_time");
            boolean archived = rs.getBoolean("is_archived");

            File file = archived ? getFileInArchiveDir(fileName) : getFileInIncomingDir(fileName);
            try {
                return new DaybreakFile(file, stagedDateTime, archived);
            }
            catch ( IOException | InvalidArgumentException ex ){
                logger.error(ex.getMessage());
                return null;
            }
        }
    }

    private class DaybreakFragmentRowMapper implements RowMapper<DaybreakFragment>{

        private DaybreakReport<DaybreakFile> daybreakFiles;

        public DaybreakFragmentRowMapper(DaybreakReport<DaybreakFile> daybreakReport){
            this.daybreakFiles = daybreakReport;
        }

        @Override
        public DaybreakFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            BillId billId = new BillId(rs.getString("bill_print_no") + rs.getString("bill_active_version"),
                            new SessionYear(rs.getInt("bill_session_year")));
            DaybreakFile daybreakFile = daybreakFiles.getDaybreakDoc(rs.getString("filename"));
            String fragmentText = rs.getString("fragment_text");

            return new DaybreakFragment(billId, daybreakFile, fragmentText);
        }
    }

    private class PageFileEntryRowMapper implements RowMapper<PageFileEntry>{
        private DaybreakFile pageFile;

        public PageFileEntryRowMapper(DaybreakFile pageFile){
            this.pageFile = pageFile;
        }

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

            LocalDate billPublishDate = getLocalDate(rs, "bill_publish_date");
            int pageCount = rs.getInt("page_count");

            return new PageFileEntry(senateBillId, assemblyBillId, pageFile, billPublishDate, pageCount);
        }
    }

    /** --- Internal Methods --- */

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
     * Moves the file into the destination quietly.
     */
    private void moveFile(File sourceFile, File destFile) throws IOException {
        if (destFile.exists()) {
            FileUtils.deleteQuietly(destFile);
        }
        FileUtils.moveFile(sourceFile, destFile);
    }

    /** --- Param Mappers --- */

    private MapSqlParameterSource getReportDateParams(LocalDate reportDate){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reportDate", toDate(reportDate));
        return params;
    }

    private MapSqlParameterSource getDaybreakFragmentIdParams(DaybreakFragmentId daybreakFragmentId){
        MapSqlParameterSource params = getReportDateParams(daybreakFragmentId.getReportDate());
        params.addValue("billPrintNo", daybreakFragmentId.getBaseBillId().getBasePrintNo());
        params.addValue("billSessionYear", daybreakFragmentId.getBaseBillId().getSession().getYear());
        return params;
    }
}
