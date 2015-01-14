package gov.nysenate.openleg.dao.law.data;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.dao.law.data.SqlLawFileQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;
import static gov.nysenate.openleg.util.FileIOUtils.safeListFiles;
import static java.util.stream.Collectors.toList;

@Repository
public class SqlFsLawFileDao extends SqlBaseDao implements LawFileDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsLawFileDao.class);

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
    public List<LawFile> getIncomingLawFiles(SortOrder sortByDate, LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(safeListFiles(this.incomingLawDir, false, null));
        List<LawFile> lawFiles = files.stream().map(LawFile::new).collect(toList());

        // Use the comparator defined in LawFile to do the sorting
        sortLaws(sortByDate, lawFiles);
        lawFiles = LimitOffset.limitList(lawFiles, limitOffset);
        return lawFiles;
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
            moveFile(file, archiveFile);
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
     * Use the comparator defined in LawFile to do the sorting, using the reverse comparator
     * if indicated by the sortByDate param.
     */
    private void sortLaws(SortOrder sortByDate, List<LawFile> lawFiles) {
        if (sortByDate.equals(SortOrder.ASC)) {
            Collections.sort(lawFiles);
        }
        else {
            Collections.sort(lawFiles, Collections.reverseOrder());
        }
    }

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

    private RowMapper<LawFile> lawFileRowMapper = (rs, rowNum) -> {
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