package gov.nysenate.openleg.legislation.transcripts;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.FileIOUtils;
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
import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.SqlHearingFileQuery.*;
import static gov.nysenate.openleg.legislation.transcripts.session.dao.SqlTranscriptFileQuery.*;

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
        BasicSqlQuery updateQuery = isHearing() ? UPDATE_HEARING_FILE : UPDATE_TRANSCRIPT_FILE;
        BasicSqlQuery insertQuery = isHearing() ? INSERT_HEARING_FILE : INSERT_TRANSCRIPT_FILE;
        if (jdbcNamed.update(updateQuery.getSql(schema()), params) == 0) {
            jdbcNamed.update(insertQuery.getSql(schema()), params);
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
        FileIOUtils.moveFile(stagedFile, archiveFile);
        file.setFile(archiveFile);
        file.setArchived(true);
    }

    @Override
    public List<T> getPendingFiles() {
        BasicSqlQuery getPendingQuery = isHearing() ?
                GET_PENDING_HEARING_FILES : GET_PENDING_TRANSCRIPT_FILES;
        var temp = jdbcNamed.query(getPendingQuery.getSql(schema()),
                new TranscriptFileRowMapper());
        Collections.sort(temp);
        return LimitOffset.limitList(temp, LimitOffset.FIFTY);
    }

    private MapSqlParameterSource getTranscriptFileParams(T transcriptFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(isHearing() ? "filename" : "fileName", transcriptFile.getFileName());
        params.addValue("processedDateTime", toDate(transcriptFile.getProcessedDateTime()));
        params.addValue("processedCount", transcriptFile.getProcessedCount());
        params.addValue("pendingProcessing", transcriptFile.isPendingProcessing());
        params.addValue("archived", transcriptFile.isArchived());
        return params;
    }

    private final class TranscriptFileRowMapper implements RowMapper<T> {
        @Override
        public T mapRow(ResultSet rs, int i) throws SQLException {
            String fileName = rs.getString(isHearing() ? "filename" : "file_name");
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
                logger.error("Transcript file " + fileName + " was not found in the expected location.", ex);
            }
            return transcriptFile;
        }
    }
}
