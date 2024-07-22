package gov.nysenate.openleg.legislation.transcripts;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.legislation.transcripts.hearing.SqlAbstractTranscriptFileQuery.*;

public abstract class SqlAbstractTranscriptFileDao<T extends AbstractTranscriptsFile>
        extends SqlBaseDao implements TranscriptFileDaoInterface<T> {
    private static final Logger logger = LoggerFactory.getLogger(SqlAbstractTranscriptFileDao.class);
    private File incomingDir;
    private File archiveDir;

    protected abstract boolean isHearing();

    protected abstract T getFile(File file) throws FileNotFoundException;

    @PostConstruct
    private void init() {
        String child = isHearing() ? "hearing_transcripts" : "session_transcripts";
        incomingDir = new File(environment.getStagingDir(), child);
        archiveDir = new File(environment.getArchiveDir(), child);
    }

    @Override
    public List<T> getIncomingFiles() throws IOException {
        Collection<File> files = FileIOUtils.safeListFiles(incomingDir, false, null);
        List<T> transcriptFiles = new ArrayList<>();
        for (File file : files)
            transcriptFiles.add(getFile(file));
        // Sort files for consistent processing.
        Collections.sort(transcriptFiles);
        return LimitOffset.limitList(transcriptFiles, LimitOffset.FIFTY);
    }

    @Override
    public void updateFile(T file) {
        MapSqlParameterSource params = getTranscriptFileParams(file);
        if (jdbcNamed.update(UPDATE_TRANSCRIPT_FILE.getSql(schema(), isHearing()), params) == 0) {
            jdbcNamed.update(INSERT_TRANSCRIPT_FILE.getSql(schema(), isHearing()), params);
        }
    }

    @Override
    public void archiveFile(T file) throws IOException {
        File stagedFile = file.getFile();
        if (!stagedFile.getParentFile().equals(incomingDir)) {
            throw new FileNotFoundException("Transcript file " + stagedFile +
                    " must be in the incoming transcripts directory in order to be archived.");
        }
        File archiveFile = new File(archiveDir, file.getFileName());
        if (!FileUtils.contentEquals(stagedFile, archiveFile)) {
            clearArchiveSpot(archiveFile);
        }
        FileIOUtils.moveFile(stagedFile, archiveFile);
        file.setFile(archiveFile);
        file.setArchived(true);
    }

    @Override
    public List<T> getPendingFiles() {
        var temp = jdbcNamed.query(GET_PENDING_TRANSCRIPT_FILES.getSql(schema(), isHearing()),
                new TranscriptFileRowMapper());
        Collections.sort(temp);
        return LimitOffset.limitList(temp, LimitOffset.FIFTY);
    }

    private MapSqlParameterSource getTranscriptFileParams(T transcriptFile) {
        return new MapSqlParameterSource().addValue("filename", transcriptFile.getFileName())
                .addValue("processedDateTime", toDate(transcriptFile.getProcessedDateTime()))
                .addValue("processedCount", transcriptFile.getProcessedCount())
                .addValue("pendingProcessing", transcriptFile.isPendingProcessing())
                .addValue("archived", transcriptFile.isArchived());
    }

    private void clearArchiveSpot(File archiveFile) throws IOException {
        if (archiveFile.exists()) {
            File archiveFileDest = new File(archiveDir, archiveFile.getName() + "_old");
            clearArchiveSpot(archiveFileDest);
            logger.warn("{} already exists in the transcript archive. Saving under a new name...", archiveFile.getName());
            FileIOUtils.moveFile(archiveFile, archiveFileDest);
            var params = new MapSqlParameterSource("originalName", archiveFile.getName())
                    .addValue("newName", archiveFileDest.getName());
            jdbcNamed.update(RENAME_TRANSCRIPT_FILE.getSql(schema(), isHearing()), params);
        }
    }

    private final class TranscriptFileRowMapper implements RowMapper<T> {
        @Override
        public T mapRow(ResultSet rs, int i) throws SQLException {
            String fileName = rs.getString("filename");
            boolean archived = rs.getBoolean("archived");

            File file = new File(archived ? archiveDir : incomingDir, fileName);
            T transcriptFile = null;
            try {
                transcriptFile = getFile(file);
                transcriptFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
                transcriptFile.setProcessedCount(rs.getInt("processed_count"));
                transcriptFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                transcriptFile.setPendingProcessing(rs.getBoolean("pending_processing"));
                transcriptFile.setArchived(archived);
            } catch (FileNotFoundException ex) {
                logger.error("Transcript file {} was not found in the expected location.", fileName, ex);
            }
            return transcriptFile;
        }
    }
}
