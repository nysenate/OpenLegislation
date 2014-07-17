package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
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
import static gov.nysenate.openleg.util.FileHelper.safeListFiles;

/**
 * Sql / File System implementation of SobiFileDao.
 *
 * SOBI files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SobiFile instances are constructed
 * utilizing both data sources.
 */
@Repository
public class SqlSobiFileDao extends SqlBaseDao implements SobiFileDao
{
    private static Logger logger = Logger.getLogger(SqlSobiFileDao.class);

    /** The database table where the sobi files are recorded */
    private static final String SOBI_FILE_TABLE = "sobi_file";

    /** Directory where new sobi files come in from external sources. */
    protected File stagingSobiDir;

    /** Directory where sobi files that are awaiting processing are stored. */
    protected File workingSobiDir;

    /** Directory where sobi files that have been processed are stored. */
    protected File archiveSobiDir;

    public SqlSobiFileDao() {}

    @PostConstruct
    protected void init() {
        this.stagingSobiDir = new File(super.environment.getStagingDirectory(), "sobis");
        this.workingSobiDir = new File(super.environment.getWorkingDirectory(), "sobis");
        this.archiveSobiDir = new File(super.environment.getArchiveDirectory(), "sobis");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc }*/
    @Override
    public SobiFile getSobiFile(String fileName) {
        SobiFile sobiFile = null;
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", Arrays.asList(fileName));
        return jdbcNamed.queryForObject(
            SqlSobiFileQuery.GET_SOBI_FILES_BY_FILE_NAMES_SQL.getSql(schema()), params, new SobiFileRowMapper());
    }

    /** {@inheritDoc }*/
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

    /** {@inheritDoc }*/
    @Override
    public List<SobiFile> getSobiFilesDuring(Date start, Date end, boolean processedOnly, SortOrder sortByPubDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", start);
        params.addValue("endDate", end);
        params.addValue("processedOnly", processedOnly);
        return jdbcNamed.query(GET_SOBI_FILES_DURING_SQL.getSql(schema()) + orderBy("published_date_time", sortByPubDate),
                               params, new SobiFileRowMapper());
    }

    /** {@inheritDoc }*/
    @Override
    public List<SobiFile> getPendingSobiFiles(SortOrder sortByPubDate, int limit, int offset) {
        return jdbc.query(GET_PENDING_SOBI_FILES_SQL.getSql(schema()) +
               orderBy("published_date_time", sortByPubDate) + limitOffset(limit, offset), new SobiFileRowMapper());
    }

    /** {@inheritDoc }*/
    @Override
    public void stageSobiFiles(boolean allowReStaging) throws IOException {
        logger.info("Performing staging of new SOBI files...");
        int filesStaged = 0;
        for (File file : safeListFiles(this.stagingSobiDir, null, false)) {
            SobiFile sobiFile = new SobiFile(file);
            sobiFile.setStagedDateTime(new Date());
            sobiFile.setPendingProcessing(true);
            try {
                insertSOBIMetadataInDatabase(sobiFile);
                moveFileToWorking(file);
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
                        existingSobiFile.setPendingProcessing(true);
                        updateSobiFile(existingSobiFile);
                        moveFileToWorking(file);
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

    /** {@inheritDoc }*/
    @Override
    public boolean updateSobiFile(SobiFile sobiFile) {
        if (sobiFile != null) {
            try {
                int affected = updateSOBIMetadataInDatabase(sobiFile);
                if (affected > 0) {
                    if (!sobiFile.isPendingProcessing()) {
                        File workFile = getFileInWorkingDirectory(sobiFile.getFileName());
                        if (workFile.exists()) {
                            moveFiletoArchive(workFile, sobiFile.getPublishedDateTime());
                        }
                    }
                    return true;
                }
                return false;
            }
            catch (DataAccessException ex) {
                logger.error("Failed to update SobiFile record with file name " + sobiFile.getFileName(), ex);
            }
            catch (IOException ex) {
                logger.error("Failed to update SOBI file " + sobiFile.getFileName() + " in file system!", ex);
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Supplied SobiFile object is null. Cannot persist to database.");
        }
    }

    /** DANGER - Wipes everything (will get rid of this method) */
    public void deleteAll() throws IOException {
        jdbc.execute("TRUNCATE " + table(SOBI_FILE_TABLE) + " CASCADE");
        if (this.workingSobiDir.exists()) {
            FileUtils.cleanDirectory(this.workingSobiDir);
        }
        if (this.archiveSobiDir.exists()) {
            FileUtils.cleanDirectory(this.archiveSobiDir);
        }
    }

    /** --- Helper Classes --- */

    /**
     * Maps rows from the sobi table to SobiFile objects.
     */
    protected class SobiFileRowMapper implements RowMapper<SobiFile> {
        private String pfx = "";

        public SobiFileRowMapper(){}
        public SobiFileRowMapper(String pfx) {
            this.pfx = pfx;
        }

        @Override
        public SobiFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString(pfx + "file_name");
            Date publishedDateTime = rs.getTimestamp(pfx + "published_date_time");
            boolean pendingProcessing = rs.getBoolean(pfx + "pending_processing");
            File file = getSOBIFileFromFileSystem(fileName, publishedDateTime, pendingProcessing);
            try {
                SobiFile sobiFile = new SobiFile(file);
                sobiFile.setPendingProcessing(pendingProcessing);
                sobiFile.setStagedDateTime(rs.getTimestamp(pfx + "staged_date_time"));
                sobiFile.setProcessedDateTime(rs.getTimestamp(pfx + "processed_date_time"));
                sobiFile.setProcessedCount(rs.getInt(pfx + "processed_count"));
                return sobiFile;
            }
            catch (FileNotFoundException ex) {
                logger.error("SOBI file " + rs.getString("file_name") + " was not found in the expected location! \n" +
                        "This could be a result of modifications to the sobi file directory that were not synced with " +
                        "the database. ", ex);
            }
            catch (IOException ex) {
                logger.error(ex);
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
        jdbcNamed.update(SqlSobiFileQuery.INSERT_SOBI_FILE.getSql(schema()), getSOBIInsertUpdateParams(sobiFile));
    }

    /**
     * Updates the metadata of the SobiFile into the database.
     * @throws java.lang.IllegalArgumentException if sobiFile is null
     */
    private int updateSOBIMetadataInDatabase(SobiFile sobiFile) throws DataAccessException {
        return jdbcNamed.update(SqlSobiFileQuery.UPDATE_SOBI_FILE.getSql(schema()), getSOBIInsertUpdateParams(sobiFile));
    }

    /**
     * Fetch actual SOBI file from the file system. It will either be in the working or archive
     * directory based on the pendingProcessing flag.
     * @param fileName String - File name of the SOBI file.
     * @param pendingProcessing boolean - Indicates where to look for the SOBI file.
     * @return File
     */
    private File getSOBIFileFromFileSystem(String fileName, Date publishedDateTime, boolean pendingProcessing) {
        return (pendingProcessing) ? getFileInWorkingDirectory(fileName) : getFileInArchiveDirectory(fileName, publishedDateTime);
    }

    /** Overload for convenience. @see #getSOBIFileFromFileSystem(String, java.util.Date, boolean) */
    private File getSOBIFileFromFileSystem(SobiFile sobiFile) {
        return getSOBIFileFromFileSystem(sobiFile.getFileName(), sobiFile.getPublishedDateTime(), sobiFile.isPendingProcessing());
    }

    /**
     * Moves the file into the working SOBI file directory.
     * @param file File
     */
    private void moveFileToWorking(File file) throws IOException {
        File workingFile = getFileInWorkingDirectory(file.getName());
        if (workingFile.exists()) {
            FileUtils.deleteQuietly(workingFile);
        }
        FileUtils.moveFile(file, workingFile);
    }

    /**
     * Moves the file into the archive SOBI file directory.
     * @param file File
     */
    private void moveFiletoArchive(File file, Date publishedDateTime) throws IOException {
        File archiveFile = getFileInArchiveDirectory(file.getName(), publishedDateTime);
        if (archiveFile.exists()) {
            FileUtils.deleteQuietly(archiveFile);
        }
        FileUtils.moveFile(file, archiveFile);
    }

    /**
     * Delete any traces of the SOBI file (either in work or archive directory) from the file system.
     * @param fileName String
     * @param publishedDateTime Date
     */
    private void deleteSOBIFileFromFileSystem(String fileName, Date publishedDateTime) {
        FileUtils.deleteQuietly(getFileInWorkingDirectory(fileName));
        FileUtils.deleteQuietly(getFileInArchiveDirectory(fileName, publishedDateTime));
    }

    /**
     * Get file handle from the sobi working directory.
     * @param fileName String
     * @return File
     */
    private File getFileInWorkingDirectory(String fileName) {
        return new File(this.workingSobiDir, fileName);
    }

    /**
     * Get file handle from the sobi archive directory.
     * @param fileName String
     * @param publishedDateTime Date
     * @return File
     */
    private File getFileInArchiveDirectory(String fileName, Date publishedDateTime) {
        String year = Integer.toString(new LocalDate(publishedDateTime).getYear());
        File dir = new File(this.archiveSobiDir, year);
        return new File(dir, fileName);
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFile values for use in update/insert queries.
     * @param sobiFile SobiFile
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource getSOBIInsertUpdateParams(SobiFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sobiFile.getFileName());
        params.addValue("publishedDateTime", toTimestamp(sobiFile.getPublishedDateTime()));
        params.addValue("processedDateTime", toTimestamp(sobiFile.getProcessedDateTime()));
        params.addValue("stagedDateTime", toTimestamp(sobiFile.getStagedDateTime()));
        params.addValue("processedCount", sobiFile.getProcessedCount());
        params.addValue("pendingProcessing", sobiFile.isPendingProcessing());
        return params;
    }
}