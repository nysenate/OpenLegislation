package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.SqlHearingFileQuery.*;

@Repository
public class SqlFsHearingFileDao extends SqlBaseDao implements HearingFileDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlFsHearingFileDao.class);

    /** Directory where new HearingFiles come in from external sources. */
    private File incomingHearingDir;

    /** Directory where we store HearingFiles that have been processed. */
    private File archiveHearingDir;

    @PostConstruct
    private void init() {
        incomingHearingDir = new File(environment.getStagingDir(), "hearing_transcripts");
        archiveHearingDir = new File(environment.getArchiveDir(), "hearing_transcripts");
    }

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public List<HearingFile> getIncomingHearingFiles(LimitOffset limOff) throws IOException {
        List<File> files = new ArrayList<>(FileIOUtils.safeListFiles(incomingHearingDir, false, null));
        List<HearingFile> hearingFiles = new ArrayList<>();
        for (File file : files)
            hearingFiles.add(new HearingFile(file));
        // Sort files for consistent processing.
        Collections.sort(hearingFiles);
        return LimitOffset.limitList(hearingFiles, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearingFile(HearingFile hearingFile) {
        MapSqlParameterSource params = getHearingFileParams(hearingFile);
        if (jdbcNamed.update(UPDATE_PUBLIC_HEARING_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_PUBLIC_HEARING_FILE.getSql(schema()), params);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void archiveHearingFile(HearingFile hearingFile) throws IOException {
        File stagedFile = hearingFile.getFile();
        if (stagedFile.getParentFile().equals(incomingHearingDir)) {
            File archiveFile = new File(archiveHearingDir, hearingFile.getFileName());
            FileIOUtils.moveFile(stagedFile, archiveFile);
            hearingFile.setFile(archiveFile);
            hearingFile.setArchived(true);
        }
        else {
            throw new FileNotFoundException("HearingFile " + stagedFile + " must be in the incoming" +
                                            "hearings directory in order to be archived.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<HearingFile> getPendingHearingFile(LimitOffset limOff) {
        var temp = jdbcNamed.query(SELECT_PENDING_PUBLIC_HEARING_FILES.getSql(schema()),
                new HearingFileRowMapper());
        Collections.sort(temp);
        return LimitOffset.limitList(temp, limOff);
    }

    /** --- Helper Classes --- */

    private class HearingFileRowMapper implements RowMapper<HearingFile> {
        @Override
        public HearingFile mapRow(ResultSet rs, int i) throws SQLException {
            String fileName = rs.getString("filename");
            boolean archived = rs.getBoolean("archived");

            File file = archived ? getFileInArchiveDir(fileName) : getFileInIncomingDir(fileName);
            HearingFile hearingFile = null;
            try {
                hearingFile = new HearingFile(file);
                hearingFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
                hearingFile.setProcessedCount(rs.getInt("processed_count"));
                hearingFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                hearingFile.setPendingProcessing(rs.getBoolean("pending_processing"));
                hearingFile.setArchived(rs.getBoolean("archived"));
            }
            catch (FileNotFoundException ex) {
                logger.error("Hearing File " + fileName + " was not found in the expected location.", ex);
            }

            return hearingFile;
        }

        private File getFileInArchiveDir(String fileName) {
            return new File(archiveHearingDir, fileName);
        }

        private File getFileInIncomingDir(String fileName) {
            return new File(incomingHearingDir, fileName);
        }
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getHearingFileParams(HearingFile hearingFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", hearingFile.getFileName());
        params.addValue("processedDateTime", toDate(hearingFile.getProcessedDateTime()));
        params.addValue("processedCount", hearingFile.getProcessedCount());
        params.addValue("pendingProcessing", hearingFile.isPendingProcessing());
        params.addValue("archived", hearingFile.isArchived());
        return params;
    }
}
