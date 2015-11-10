package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.util.FileIOUtils;
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
import java.util.List;

import static gov.nysenate.openleg.dao.transcript.SqlTranscriptFileQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlFsTranscriptFileDao extends SqlBaseDao implements TranscriptFileDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsTranscriptFileDao.class);

    /** Directory where new transcripts come in from external sources */
    private File incomingTranscriptDir;

    /** Directory where we store transcript files that have been processed */
    private File archiveTranscriptDir;

    @PostConstruct
    private void init() {
        incomingTranscriptDir = new File(environment.getStagingDir(), "session_transcripts");
        archiveTranscriptDir = new File(environment.getArchiveDir(), "session_transcripts");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public List<TranscriptFile> getIncomingTranscriptFiles(LimitOffset limOff) throws IOException {
        List<File> files = new ArrayList<>(FileIOUtils.safeListFiles(incomingTranscriptDir, false, null));
        files = LimitOffset.limitList(files, limOff);

        List<TranscriptFile> transcriptFiles = new ArrayList<>();
        for (File file : files) {
            transcriptFiles.add(new TranscriptFile(file));
        }
        return transcriptFiles;
    }

    @Override
    public void updateTranscriptFile(TranscriptFile transcriptFile) {
        MapSqlParameterSource params = getTranscriptFileParams(transcriptFile);
        if (jdbcNamed.update(UPDATE_TRANSCRIPT_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_TRANSCRIPT_FILE.getSql(schema()), params);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void archiveAndUpdateTranscriptFile(TranscriptFile transcriptFile) throws IOException {
        File stagedFile = transcriptFile.getFile();
        if (stagedFile.getParentFile().compareTo(incomingTranscriptDir) == 0) {
            File archiveFile = new File(archiveTranscriptDir, transcriptFile.getFileName());
            moveFile(stagedFile, archiveFile);
            transcriptFile.setFile(archiveFile);
            transcriptFile.setArchived(true);
            updateTranscriptFile(transcriptFile);
        }
        else {
            throw new FileNotFoundException(
                    "TranscriptFile " + stagedFile + " must be in the incoming transcripts directory in order to be archived.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limOff) {
        return jdbcNamed.query(GET_PENDING_TRANSCRIPT_FILES.getSql(schema(), limOff), new TranscriptFileRowMapper());
    }

    /** --- Internal Methods --- */

    private File getFileInArchiveDir(String fileName) {
        return new File(archiveTranscriptDir, fileName);
    }

    private File getFileInIncomingDir(String fileName) {
        return new File(incomingTranscriptDir, fileName);
    }

    /** --- Helper Classes --- */

    protected class TranscriptFileRowMapper implements RowMapper<TranscriptFile> {

        @Override
        public TranscriptFile mapRow(ResultSet rs, int i) throws SQLException {
            String fileName = rs.getString("file_name");
            boolean archived = rs.getBoolean("archived");

            File file = archived ? getFileInArchiveDir(fileName) : getFileInIncomingDir(fileName);
            TranscriptFile transcriptFile = null;
            try {
                transcriptFile = new TranscriptFile(file);
                transcriptFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
                transcriptFile.setProcessedCount(rs.getInt("processed_count"));
                transcriptFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                transcriptFile.setPendingProcessing(rs.getBoolean("pending_processing"));
                transcriptFile.setArchived(archived);
            } catch (FileNotFoundException ex) {
                logger.error("Transcript File " + fileName + " was not found in the expected location.", ex);
            }

            return transcriptFile;
        }
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getTranscriptFileParams(TranscriptFile transcriptFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", transcriptFile.getFileName());
        params.addValue("processedDateTime", toDate(transcriptFile.getProcessedDateTime()));
        params.addValue("processedCount", transcriptFile.getProcessedCount());
        params.addValue("pendingProcessing", transcriptFile.isPendingProcessing());
        params.addValue("archived", transcriptFile.isArchived());
        return params;
    }

}
