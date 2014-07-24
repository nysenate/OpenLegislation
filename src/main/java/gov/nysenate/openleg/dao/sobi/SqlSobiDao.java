package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.Limit;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.nysenate.openleg.dao.sobi.SqlSobiFileQuery.*;
import static gov.nysenate.openleg.util.FileHelper.*;

/**
 * Sobi files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SobiFile instances are constructed
 * utilizing both data sources.
 */
@Repository
public class SqlSobiDao extends SqlBaseDao implements SobiFileDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSobiDao.class);

    /** Directory where new sobi files come in from external sources. */
    protected File incomingSobiDir;

    /** Directory where sobi files that have been processed are stored. */
    protected File archiveSobiDir;

    public SqlSobiDao() {}

    @PostConstruct
    protected void init() {
        this.incomingSobiDir = new File(environment.getStagingDirectory(), "sobis");
        this.archiveSobiDir = new File(environment.getArchiveDirectory(), "sobis");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public SobiFile getSobiFile(String fileName) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", Arrays.asList(fileName));
        return jdbcNamed.queryForObject(
            SqlSobiFileQuery.GET_SOBI_FILES_BY_FILE_NAMES_SQL.getSql(schema()), params, new SobiFileRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, SobiFile> getSobiFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SobiFile> sobiFileMap = new HashMap<>();
        List<SobiFile> sobiList = jdbcNamed.query(GET_SOBI_FILES_BY_FILE_NAMES_SQL.getSql(schema()),
                                                  params, new SobiFileRowMapper());
        for (SobiFile sobiFile : sobiList) {
            sobiFileMap.put(sobiFile.getFileName(), sobiFile);
        }
        return sobiFileMap;
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFile> getSobiFilesDuring(Date start, Date end, boolean processedOnly, SortOrder sortByPubDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", start);
        params.addValue("endDate", end);
        params.addValue("processedOnly", processedOnly);
        return jdbcNamed.query(GET_SOBI_FILES_DURING_SQL.getSql(schema()) + orderBy("published_date_time", sortByPubDate),
                               params, new SobiFileRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFile> getPendingSobiFiles(SortOrder sortByPubDate, int limit, int offset) {
        return jdbc.query(GET_PENDING_SOBI_FILES_SQL.getSql(schema()) +
               orderBy("published_date_time", sortByPubDate) + limitOffset(limit, offset), new SobiFileRowMapper());
    }

    public List<SobiFile> getIncomingSobiFiles(SortOrder sortByFileName, Limit limit) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(this.incomingSobiDir, false, null));
        if (limit.hasLimit()) {
            files = new ArrayList<>(files.subList(limit.getOffset(), limit.getOffset() + limit.getSize() - 1));
        }
        List<SobiFile> sobiFiles = new ArrayList<>();
        for (File file : files) {
            sobiFiles.add(new SobiFile(file));
        }
        return sobiFiles;
    }

    /** {@inheritDoc} */
    @Override
    public void stageSobiFiles(boolean allowReStaging) throws IOException {
        logger.info("Performing staging of new SOBI files...");
        int filesStaged = 0;
        for (File file : safeListFiles(this.incomingSobiDir, null, false)) {
            SobiFile sobiFile = new SobiFile(file);
            sobiFile.setStagedDateTime(new Date());
            try {
                insertSOBIMetadataInDatabase(sobiFile);
                filesStaged++;
                logger.debug("Staged " + sobiFile);
            }
            catch (DuplicateKeyException ex) {
                if (allowReStaging) {
                    // The SOBI file has been staged once before so we want to re-stage it with the contents of the
                    // new file which may or may not be identical. This should rarely occur in production but can
                    // be helpful during development.
                    SobiFile existingSobiFile = getSobiFile(sobiFile.getFileName());
                    if (existingSobiFile != null) {
                        existingSobiFile.setStagedDateTime(new Date());
                        updateSobiFile(existingSobiFile);
                        filesStaged++;
                        logger.debug("Re-staged " + existingSobiFile + " with latest file contents.");
                    }
                    else {
                        logger.warn("Failed to stage " + sobiFile + " because a prior record exists but cannot be retrieved!");
                    }
                }
                else {
                    logger.debug("Ignoring " + sobiFile + " since re-staging is disabled and file was previously processed.");
                }
            }
        }
        logger.info("Staging of " + filesStaged + " SOBI files completed.");
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateSobiFile(SobiFile sobiFile) {
        if (sobiFile != null) {
            try {
                int affected = updateSOBIMetadataInDatabase(sobiFile);
                if (affected > 0) {
//                    if (!sobiFile.isPendingProcessing()) {
//                        File workFile = getFileInWorkingDirectory(sobiFile.getFileName());
//                        if (workFile.exists()) {
//                            moveFileToArchive(workFile, sobiFile.getPublishedDateTime());
//                        }
//                    }
                    return true;
                }
                return false;
            }
            catch (DataAccessException ex) {
                logger.error("Failed to update SobiFile record with file name " + sobiFile.getFileName(), ex);
            }
//            catch (IOException ex) {
//                logger.error("Failed to update SOBI file " + sobiFile.getFileName() + " in file system!", ex);
//            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Supplied SobiFile object is null. Cannot persist to database.");
        }
    }

    /** --- Helper Classes --- */

    /**
     * Maps rows from the sobi table to SobiFile objects.
     */
    protected class SobiFileRowMapper implements RowMapper<SobiFile>
    {
        public SobiFileRowMapper(){}

        @Override
        public SobiFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            Date publishedDateTime = rs.getTimestamp("published_date_time");
            File file = getFileInArchiveDirectory(fileName, publishedDateTime);
            String encoding = rs.getString("encoding");
            try {
                SobiFile sobiFile = new SobiFile(file, encoding);
                sobiFile.setStagedDateTime(rs.getTimestamp("staged_date_time"));
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

    /** --- Internal Methods --- */

    /**
     * Saves the metadata of the SobiFile into the database.
     * @throws java.lang.IllegalArgumentException if sobiFile is null
     */
    private void insertSOBIMetadataInDatabase(SobiFile sobiFile) throws DataAccessException {
        jdbcNamed.update(SqlSobiFileQuery.INSERT_SOBI_FILE.getSql(schema()), getSobiInsertUpdateParams(sobiFile));
    }

    /**
     * Updates the metadata of the SobiFile into the database.
     * @throws java.lang.IllegalArgumentException if sobiFile is null
     */
    private int updateSOBIMetadataInDatabase(SobiFile sobiFile) throws DataAccessException {
        return jdbcNamed.update(SqlSobiFileQuery.UPDATE_SOBI_FILE.getSql(schema()), getSobiInsertUpdateParams(sobiFile));
    }

    /**
     * Get file handle from the sobi archive directory.
     */
    private File getFileInArchiveDirectory(String fileName, Date publishedDateTime) {
        String year = Integer.toString(new LocalDate(publishedDateTime).getYear());
        File dir = new File(this.archiveSobiDir, year);
        return new File(dir, fileName);
    }

    /**
     * Moves the file into the archive SOBI file directory.
     */
    private void moveFileToArchive(File file, Date publishedDateTime) throws IOException {
        File archiveFile = getFileInArchiveDirectory(file.getName(), publishedDateTime);
        if (archiveFile.exists()) {
            FileUtils.deleteQuietly(archiveFile);
        }
        FileUtils.moveFile(file, archiveFile);
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFile values for use in update/insert queries.
     */
    private MapSqlParameterSource getSobiInsertUpdateParams(SobiFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sobiFile.getFileName());
        params.addValue("encoding", sobiFile.getEncoding());
        params.addValue("publishedDateTime", sobiFile.getPublishedDateTime());
        params.addValue("stagedDateTime", sobiFile.getStagedDateTime());
        return params;
    }
}