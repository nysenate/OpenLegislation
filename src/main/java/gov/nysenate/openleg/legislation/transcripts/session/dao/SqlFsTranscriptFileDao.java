package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.legislation.transcripts.session.dao.SqlTranscriptFileQuery.*;

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
        transcriptFiles.sort(Comparator.comparing(TranscriptFile::getFileName));
        return transcriptFiles;
    }

    @Override
    public void updateTranscriptFile(TranscriptFile transcriptFile) {
        MapSqlParameterSource params = getTranscriptFileParams(transcriptFile);
        try {
            TranscriptFile fetchedFile = jdbcNamed.queryForObject(GET_BY_FILENAME.getSql(schema()), params,
                    new TranscriptFileRowMapper());
            // A TranscriptFile with no dateTime has not been processed, and must be replaced to be renamed.
            if (fetchedFile != null && fetchedFile.getDateTime() == null) {
                jdbcNamed.update(DELETE_BY_FILENAME.getSql(schema()), params);
                renameFile(transcriptFile);
                params.addValue("fileName", transcriptFile.getFileName());
            }
        }
        catch (EmptyResultDataAccessException ignored) {}
        // TODO: check that updates work correctly, with ir without same filename.
        if (jdbcNamed.update(UPDATE_TRANSCRIPT_FILE.getSql(schema()), params) == 0)
            jdbcNamed.update(INSERT_TRANSCRIPT_FILE.getSql(schema()), params);
    }

    private int pastVersions(TranscriptFile transcriptFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("dateTime", transcriptFile.getDateTime());
        Integer ret = jdbcNamed.queryForObject(OLD_FILE_COUNT.getSql(schema()), params, Integer.class);
        return ret == null ? 0 : ret;
    }

    private void renameFile(TranscriptFile transcriptFile) {
        String currVersion = Integer.toString(pastVersions(transcriptFile) + 1);
        LocalDateTime dateTime = transcriptFile.getDateTime();
        // Remove ':' chars from file name since they are not supported in Windows.
        String trueName = dateTime.toString().replaceAll(":", "") + ".v" + currVersion;
        File renamedFile = new File(archiveTranscriptDir, trueName);
        try {
            FileIOUtils.moveFile(transcriptFile.getFile(), renamedFile);
            transcriptFile.setFile(renamedFile);
        }
        catch (IOException e) {
            logger.error("TranscriptFile " + transcriptFile.getFileName() + " could not be properly renamed.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void archiveTranscriptFile(TranscriptFile transcriptFile) throws IOException {
        File stagedFile = transcriptFile.getFile();
        if (stagedFile.getParentFile().equals(incomingTranscriptDir)) {
            File archiveFile = new File(archiveTranscriptDir, LocalDateTime.now().toString());
            FileIOUtils.moveFile(stagedFile, archiveFile);
            transcriptFile.setFile(archiveFile);
            transcriptFile.setArchived(true);
        }
        else {
            throw new FileNotFoundException("TranscriptFile " + stagedFile + " must be in the incoming " +
                    "transcripts directory in order to be archived.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TranscriptFile> getPendingTranscriptFiles(LimitOffset limOff) {
        return jdbcNamed.query(GET_PENDING_TRANSCRIPT_FILES.getSql(schema(), limOff), new TranscriptFileRowMapper());
    }

    /** --- Helper Classes --- */

    protected class TranscriptFileRowMapper implements RowMapper<TranscriptFile> {

        @Override
        public TranscriptFile mapRow(ResultSet rs, int i) throws SQLException {
            String fileName = rs.getString("file_name");
            boolean archived = rs.getBoolean("archived");

            File file = new File(archived ? archiveTranscriptDir : incomingTranscriptDir, fileName);
            TranscriptFile transcriptFile = null;
            try {
                transcriptFile = new TranscriptFile(file);
                transcriptFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
                transcriptFile.setOriginalFilename(rs.getString("original_filename"));
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
        params.addValue("dateTime", transcriptFile.getDateTime());
        params.addValue("originalFilename", transcriptFile.getOriginalFilename());
        return params;
    }

}
