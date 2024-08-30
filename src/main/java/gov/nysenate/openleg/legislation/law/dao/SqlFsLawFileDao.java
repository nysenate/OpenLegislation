package gov.nysenate.openleg.legislation.law.dao;

import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.processors.law.LawFile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.common.util.FileIOUtils.safeListFiles;

@Repository
public class SqlFsLawFileDao extends SqlBaseDao implements LawFileDao
{

    /** Directory where new law files come in. */
    private File incomingLawDir;

    /** Directory where law files that have been processed are stored. */
    private File archiveLawDir;

    @PostConstruct
    protected void init() {
        this.incomingLawDir = new File(environment.getStagingDir(), "laws");
        this.archiveLawDir = new File(environment.getArchiveDir(), "laws");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public List<LawFile> getIncomingLawFiles() throws IOException {
        return safeListFiles(this.incomingLawDir).stream().map(LawFile::new).toList();
    }

    @Override
    public List<LawFile> getPendingLawFiles(SortOrder sortByDate, LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("published_date_time", sortByDate, "file_name", sortByDate);
        return jdbcNamed.query(SqlLawFileQuery.GET_PENDING_LAW_FILES.getSql(schema(), orderBy, limitOffset), lawFileRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawFile(LawFile lawFile) {
        ImmutableParams lawParams = ImmutableParams.from(getLawFileParameters(lawFile));
        if (jdbcNamed.update(SqlLawFileQuery.UPDATE_LAW_FILE.getSql(schema()), lawParams) == 0) {
            jdbcNamed.update(SqlLawFileQuery.INSERT_LAW_FILE.getSql(schema()), lawParams);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void archiveAndUpdateLawFile(LawFile lawFile) throws IOException {
        // Archive the file only if it's currently in the incoming directory.
        File file = lawFile.getFile();
        if (file.getParentFile().compareTo(this.incomingLawDir) == 0) {
            File archiveFile = getFileInArchiveDir(file.getName());
            FileIOUtils.moveFile(file, archiveFile);
            lawFile.setFile(archiveFile);
            lawFile.setArchived(true);
            updateLawFile(lawFile);
        }
        else {
            throw new FileNotFoundException(
                "The source law file must be in the incoming laws directory in order to be archived.");
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get file handle from the incoming law directory.
     */
    private File getFileInIncomingDir(String fileName) {
        return new File(this.incomingLawDir, fileName);
    }

    /**
     * Get file handle from the law archive directory.
     */
    private File getFileInArchiveDir(String fileName) {
        return new File(this.archiveLawDir, fileName);
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getLawFileParameters(LawFile lawFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", lawFile.getFileName())
              .addValue("publishedDateTime", toDate(lawFile.getPublishedDate()))
              .addValue("processedDateTime", toDate(lawFile.getProcessedDateTime()))
              .addValue("processedCount", lawFile.getProcessedCount())
              .addValue("pendingProcessing", lawFile.isPendingProcessing())
              .addValue("archived", lawFile.isArchived());
        return params;
    }

    /** --- Row Mapper Instances --- */

    private final RowMapper<LawFile> lawFileRowMapper = (rs, rowNum) -> {
        String fileName = rs.getString("file_name");
        boolean isArchived = rs.getBoolean("archived");
        File file = (isArchived) ? getFileInArchiveDir(fileName) : getFileInIncomingDir(fileName);
        LawFile lawFile = new LawFile(file);
        lawFile.setArchived(isArchived);
        lawFile.setPendingProcessing(rs.getBoolean("pending_processing"));
        lawFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
        lawFile.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
        lawFile.setProcessedCount(rs.getInt("processed_count"));
        return lawFile;
    };
}