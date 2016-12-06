 gov.nysenate.openleg.dao.sobi;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileExistsException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.sobi.SqlSobiQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;
import static gov.nysenate.openleg.util.FileIOUtils.getSortedFiles;

/**
 * Sobi files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SobiFile instances are constructed
 * utilizing both data sources.
 */
@Repository
public class SqlFsSobiDao extends SqlBaseDao implements SobiDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlFsSobiDao.class);

    /** Directory where new sobi files come in from external sources. */
    private File incomingSobiDir;

    /** Directory where sobi files that have been processed are stored. */
    private File archiveSobiDir;

    @PostConstruct
    protected void init() {
        this.incomingSobiDir = new File(environment.getStagingDir(), "sobis");
        this.archiveSobiDir = new File(environment.getArchiveDir(), "sobis");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public SobiFile getSobiFile(String fileName) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", Arrays.asList(fileName));
        return jdbcNamed.queryForObject(
            GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()), params, new SobiFileRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, SobiFile> getSobiFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SobiFile> sobiFileMap = new HashMap<>();
        List<SobiFile> sobiList = jdbcNamed.query(GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()),
                                                  params, new SobiFileRowMapper());
        for (SobiFile sobiFile : sobiList) {
            sobiFileMap.put(sobiFile.getFileName(), sobiFile);
        }
        return sobiFileMap;
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<SobiFile> getSobiFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByPubDate,
                                             LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("published_date_time", sortByPubDate);
        PaginatedRowHandler<SobiFile> handler = new PaginatedRowHandler<>(limitOffset, "total_count", new SobiFileRowMapper());
        jdbcNamed.query(GET_SOBI_FILES_DURING.getSql(schema(), orderBy, limitOffset), params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFile> getIncomingSobiFiles(SortOrder sortByFileName, LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(this.incomingSobiDir, false, null));
        if (sortByFileName.equals(SortOrder.DESC)) {
            Collections.reverse(files);
        }
        files = LimitOffset.limitList(files, limitOffset);
        List<SobiFile> sobiFiles = new ArrayList<>();
        for (File file : files) {
            sobiFiles.add(new SobiFile(file));
        }
        return sobiFiles;
    }

    /** {@inheritDoc} */
    @Override
    public SobiFragment getSobiFragment(String fragmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragmentId);
        return jdbcNamed.queryForObject(
            GET_SOBI_FRAGMENT_BY_FILE_NAME.getSql(schema()), params, new SobiFragmentRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getSobiFragments(SobiFile sobiFile, SortOrder sortById) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName", sobiFile.getFileName());
        OrderBy orderBy = new OrderBy("fragment_id", sortById);
        return jdbcNamed.query(
            GET_SOBI_FRAGMENTS_BY_SOBI_FILE.getSql(schema(), orderBy, LimitOffset.ALL),
            params, new SobiFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getSobiFragments(SobiFile sobiFile, SobiFragmentType fragmentType, SortOrder sortById) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFileName", sobiFile.getFileName());
        params.addValue("fragmentType", fragmentType.name());
        OrderBy orderBy = new OrderBy("fragment_id", sortById);
        return jdbcNamed.query(
            GET_SOBI_FRAGMENTS_BY_SOBI_FILE_AND_TYPE.getSql(schema(), orderBy, LimitOffset.ALL),
            params, new SobiFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getPendingSobiFragments(SortOrder sortById, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("fragment_id", sortById);
        return jdbcNamed.query(
            GET_PENDING_SOBI_FRAGMENTS.getSql(schema(), orderBy, limOff), new SobiFragmentRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getPendingSobiFragments(ImmutableSet<SobiFragmentType> restrict, SortOrder sortById,
                                                      LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("fragment_id", sortById);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentTypes", restrict.stream().map(Enum::name).collect(Collectors.toSet()));
        return jdbcNamed.query(
            GET_PENDING_SOBI_FRAGMENTS_BY_TYPE.getSql(schema(), orderBy, limOff), params, new SobiFragmentRowMapper());
    }

    /** --- Update/Insert Methods --- */

    /** {@inheritDoc} */
    @Override
    public void archiveAndUpdateSobiFile(SobiFile sobiFile) throws IOException {
        File stageFile = sobiFile.getFile();
        // Archive the file only if the current one is residing in the incoming sobis directory.
        if (stageFile.getParentFile().compareTo(incomingSobiDir) == 0) {
            File archiveFile = getFileInArchiveDir(sobiFile.getFileName(), sobiFile.getPublishedDateTime());
            moveFile(stageFile, archiveFile);
            sobiFile.setFile(archiveFile);
            sobiFile.setArchived(true);
            updateSobiFile(sobiFile);
        }
        else {
            throw new FileNotFoundException(
                "SobiFile " + stageFile + " must be in the incoming sobis directory in order to be archived.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateSobiFile(SobiFile sobiFile) {
        MapSqlParameterSource params = getSobiFileParams(sobiFile);
        if (jdbcNamed.update(UPDATE_SOBI_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_SOBI_FILE.getSql(schema()), params);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateSobiFragment(SobiFragment fragment) {
        MapSqlParameterSource params = getSobiFragmentParams(fragment);
        if (jdbcNamed.update(UPDATE_SOBI_FRAGMENT.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_SOBI_FRAGMENT.getSql(schema()), params);
        }
    }

    /** --- Helper Classes --- */

    /**
     * Maps rows from the sobi file table to SobiFile objects.
     */
    protected class SobiFileRowMapper implements RowMapper<SobiFile>
    {
        @Override
        public SobiFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
            boolean archived = rs.getBoolean("archived");
            File file = (archived) ? getFileInArchiveDir(fileName, publishedDateTime)
                                   : getFileInIncomingDir(fileName);
            String encoding = rs.getString("encoding");
            try {
                SobiFile sobiFile = new SobiFile(file, encoding);
                sobiFile.setArchived(archived);
                sobiFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                return sobiFile;
            }
            catch (FileNotFoundException ex) {
                logger.error(
                    "SOBI file " + rs.getString("file_name") + " was not found in the expected location! \n" +
                    "This could be a result of modifications to the sobi file directory that were not synced with " +
                    "the database.", ex);
            }
            catch (IOException ex) {
                logger.error("{}", ex);
            }
            return null;
        }
    }

    /**
     * Maps rows from the sobi fragment table to SobiFragment objects.
     */
    protected class SobiFragmentRowMapper implements RowMapper<SobiFragment> {
        private String pfx = "";
        private Map<String, SobiFile> sobiFileMap = new HashMap<>();

        public SobiFragmentRowMapper() {
            this("", Collections.<SobiFile>emptyList());
        }

        public SobiFragmentRowMapper(SobiFile sobiFile) {
            this("", Arrays.asList(sobiFile));
        }

        public SobiFragmentRowMapper(String pfx, List<SobiFile> sobiFiles) {
            this.pfx = pfx;
            for (SobiFile sobiFile : sobiFiles) {
                this.sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }

        @Override
        public SobiFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sobiFileName = rs.getString(pfx + "sobi_file_name");
            // Passing the sobi file objects in the constructor is a means of caching the objects
            // so that they don't have to be re-mapped. If not supplied, an extra call will be
            // made to fetch the sobi file.
            SobiFile sobiFile = this.sobiFileMap.get(sobiFileName);
            if (sobiFile == null) {
                sobiFile = getSobiFile(sobiFileName);
            }
            SobiFragmentType type = SobiFragmentType.valueOf(rs.getString(pfx + "fragment_type").toUpperCase());
            int sequenceNo = rs.getInt(pfx + "sequence_no");
            String text = rs.getString(pfx + "text");
            SobiFragment fragment = new SobiFragment(sobiFile, type, text, sequenceNo);
            fragment.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
            fragment.setPendingProcessing(rs.getBoolean("pending_processing"));
            fragment.setProcessedCount(rs.getInt("processed_count"));
            fragment.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
            fragment.setManualFix(rs.getBoolean("manual_fix"));
            fragment.setManualFixNotes(rs.getString("manual_fix_notes"));
            return fragment;
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get file handle from incoming sobi directory.
     */
    private File getFileInIncomingDir(String fileName) {
        return new File(this.incomingSobiDir, fileName);
    }

    /**
     * Get file handle from the sobi archive directory.
     */
    private File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime) {
        String year = Integer.toString(publishedDateTime.getYear());
        File dir = new File(this.archiveSobiDir, year);
        return new File(dir, fileName);
    }

    /** --- Param Source Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFile values.
     */
    private MapSqlParameterSource getSobiFileParams(SobiFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sobiFile.getFileName());
        params.addValue("encoding", sobiFile.getEncoding());
        params.addValue("publishedDateTime", toDate(sobiFile.getPublishedDateTime()));
        params.addValue("stagedDateTime", toDate(sobiFile.getStagedDateTime()));
        params.addValue("archived", sobiFile.isArchived());
        return params;
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFragment values.
     */
    private MapSqlParameterSource getSobiFragmentParams(SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragment.getFragmentId());
        params.addValue("sobiFileName", fragment.getParentSobiFile().getFileName());
        params.addValue("publishedDateTime", toDate(fragment.getPublishedDateTime()));
        params.addValue("fragmentType", fragment.getType().name());
        params.addValue("sequenceNo", fragment.getSequenceNo());
        // Replace all null characters with empty string.
        params.addValue("text", fragment.getText().replace('\0', ' '));
        params.addValue("processedCount", fragment.getProcessedCount());
        params.addValue("processedDateTime", toDate(fragment.getProcessedDateTime()));
        params.addValue("pendingProcessing", fragment.isPendingProcessing());
        params.addValue("manualFix", fragment.isManualFix());
        params.addValue("manualFixNotes", fragment.getManualFixNotes());
        return params;
    }
}
