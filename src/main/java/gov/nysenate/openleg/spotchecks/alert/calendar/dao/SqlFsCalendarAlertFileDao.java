package gov.nysenate.openleg.spotchecks.alert.calendar.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarAlertFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.spotchecks.alert.calendar.dao.SqlCalendarAlertFileQuery.*;

@Repository
public class SqlFsCalendarAlertFileDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlFsCalendarAlertFileDao.class);

    private static final Pattern calendarAlertFilePattern =
            Pattern.compile("^(floor_cal|active_list)_alert-\\d{4}-\\d+(-\\d+)?[A-Z]?-\\d{8}T\\d{6}.html$");

    @Autowired
    private OpenLegEnvironment environment;

    private File incomingCalendarAlertDir;
    private File archiveCalendarAlertDir;

    @PostConstruct
    public void init() {
        incomingCalendarAlertDir = new File(environment.getStagingDir(), "alerts");
        archiveCalendarAlertDir = new File(environment.getArchiveDir(), "alerts");
    }

    /**
     * Retrieve all calendar alert files from the incoming directory.
     *
     * @return
     * @throws IOException
     */
    public List<CalendarAlertFile> getIncomingCalendarAlerts() throws IOException {
        List<File> files = FileIOUtils.safeListFiles(incomingCalendarAlertDir, null, false).stream()
                .filter(file -> calendarAlertFilePattern.matcher(file.getName()).matches())
                .toList();

        List<CalendarAlertFile> calendarAlertFiles = new ArrayList<>();
        for (File file : files) {
            calendarAlertFiles.add(new CalendarAlertFile(file));
        }
        return calendarAlertFiles;
    }

    /**
     * Moves the calendar alert file to the archive directory.
     *
     * @param calendarAlertFile
     * @return a new CalendarAlertFile with a reference to the new file location.
     * @throws IOException
     */
    public CalendarAlertFile archiveCalendarAlertFile(CalendarAlertFile calendarAlertFile) throws IOException {
        File stagedFile = calendarAlertFile.getFile();
        if (isInIncomingDirectory(stagedFile)) {
            calendarAlertFile = archive(stagedFile);
        }
        return calendarAlertFile;
    }

    /**
     * Update a CalendarAlertFile in the database, If its new, insert it instead.
     * @param calendarAlertFile
     */
    public void updateCalendarAlertFile(CalendarAlertFile calendarAlertFile) {
        MapSqlParameterSource params = getCalendarAlertFileParams(calendarAlertFile);
        if (jdbcNamed.update(UPDATE_CALENDAR_ALERT_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_CALENDAR_ALERT_FILE.getSql(schema()), params);
        }
    }

    /**
     * Get all CalendarAlertFiles awaiting processing.
     * @param limOff
     * @return
     */
    public List<CalendarAlertFile> getPendingCalendarAlertFiles(LimitOffset limOff) {
        return jdbcNamed.query(GET_PENDING_CALENDAR_ALERT_FILES.getSql(schema(), limOff), new CalendarAlertFileRowMapper());
    }

    private CalendarAlertFile archive(File stagedFile) throws IOException {
        File archivedFile = new File(archiveCalendarAlertDir, stagedFile.getName());
        FileIOUtils.moveFile(stagedFile, archivedFile);

        CalendarAlertFile calendarAlertFile = new CalendarAlertFile(archivedFile);
        calendarAlertFile.setArchived(true);
        return calendarAlertFile;
    }

    private boolean isInIncomingDirectory(File stagedFile) {
        return stagedFile.getParentFile().compareTo(incomingCalendarAlertDir) == 0;
    }

    protected class CalendarAlertFileRowMapper implements RowMapper<CalendarAlertFile> {
        @Override
        public CalendarAlertFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String filename = rs.getString("file_name");
            boolean archived = rs.getBoolean("archived");

            var file = new File(archived ? archiveCalendarAlertDir : incomingCalendarAlertDir, filename);
            CalendarAlertFile calendarAlertFile = null;
            try {
                calendarAlertFile = new CalendarAlertFile(file);
                calendarAlertFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
                calendarAlertFile.setProcessedCount(rs.getInt("processed_count"));
                calendarAlertFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                calendarAlertFile.setPendingProcessing(rs.getBoolean("pending_processing"));
                calendarAlertFile.setArchived(archived);
            } catch (FileNotFoundException ex) {
                logger.error("CalendarAlert File " + filename + " was not found in the expected location.", ex);
            }
            return calendarAlertFile;
        }
    }

    private static MapSqlParameterSource getCalendarAlertFileParams(CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", file.getFile().getName());
        params.addValue("processedDateTime", toDate(file.getProcessedDateTime()));
        params.addValue("processedCount", file.getProcessedCount());
        params.addValue("pendingProcessing", file.isPendingProcessing());
        params.addValue("archived", file.isArchived());
        return params;
    }
}
