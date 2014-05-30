package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.nysenate.openleg.util.FileHelper.moveFileToDirectory;
import static gov.nysenate.openleg.util.FileHelper.safeListFiles;

/**
 * Sql / File System implementation of SOBIFileDao.
 *
 * SOBI files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SOBIFile instances are constructed
 * utilizing both data sources.
 */
public class SqlSOBIFileDao extends SqlBaseDao implements SOBIFileDao
{
    private static Logger logger = Logger.getLogger(SqlSOBIFileDao.class);

    /** The database table where the sobi files are recorded */
    private static final String SOBI_FILE_TABLE = "sobi_file";

    /** Directory where new sobi files come in from external sources. */
    protected final File stagingSobiDir;

    /** Directory where sobi files that are awaiting processing are stored. */
    protected final File workingSobiDir;

    /** Directory where sobi files that have been processed are stored. */
    protected final File archiveSobiDir;

    public SqlSOBIFileDao(Environment environment) {
        super(environment);
        this.stagingSobiDir = new File(environment.getStagingDirectory(), "sobis");
        this.workingSobiDir = new File(environment.getWorkingDirectory(), "sobis");
        this.archiveSobiDir = new File(environment.getArchiveDirectory(), "sobis");
    }

    /** --- SQL Statements --- */

    private final String GET_SOBI_FILES_BY_FILE_NAMES_SQL =
        "SELECT * FROM " + table(SOBI_FILE_TABLE) + " WHERE file_name IN (:fileNames) ";

    private final String GET_SOBI_FILES_DURING_SQL =
        "SELECT * FROM " + table(SOBI_FILE_TABLE) + "\n" +
        "WHERE (published_date_time BETWEEN :startDate AND :endDate) AND (:processedOnly = false OR processed_count > 0) ";

    private final String GET_PENDING_SOBI_FILES_SQL =
        "SELECT * FROM " + table(SOBI_FILE_TABLE) + " WHERE pending_processing = true ";

    private final String INSERT_SOBI_FILE =
        "INSERT INTO " + table(SOBI_FILE_TABLE) + " (file_name, published_date_time, processed_date_time, processed_count, pending_processing) " +
        "VALUES (:fileName, :publishedDateTime, :processedDateTime, :processedCount, :pendingProcessing)";

    private final String UPDATE_SOBI_FILE =
        "UPDATE " + table(SOBI_FILE_TABLE) + "\n" +
        "SET published_date_time = :publishedDateTime," +
        "    processed_date_time = :processedDateTime," +
        "    pending_processing = :pendingProcessing," +
        "    processed_count = :processedCount " +
        "WHERE file_name = :fileName";

    /** --- Implemented Methods --- */

    /** {@inheritDoc }*/
    @Override
    public SOBIFile getSOBIFile(String fileName) {
        SOBIFile sobiFile = null;
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", Arrays.asList(fileName));
        try {
            SOBIMetadata metaData = jdbcNamed.queryForObject(GET_SOBI_FILES_BY_FILE_NAMES_SQL, params, new SOBIMetadata.Mapper());
            sobiFile = new SOBIFile(getSOBIFileFromFileSystem(metaData));
            metaData.applyToSOBIFile(sobiFile);
        }
        catch (IncorrectResultSizeDataAccessException ex) {
            logger.debug("No sobi details were found in database for " + fileName, ex);
        }
        catch (IOException ex) {
            logger.error("Possible inconsistency between file system and database! " +
                         "Failed to construct SOBIFile instance with file name " + fileName, ex);
        }
        return sobiFile;
    }

    /** {@inheritDoc }*/
    @Override
    public Map<String, SOBIFile> getSOBIFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SOBIFile> sobiFileMap = new HashMap<>();
        List<SOBIMetadata> metaDataList = jdbcNamed.query(GET_SOBI_FILES_BY_FILE_NAMES_SQL, params, new SOBIMetadata.Mapper());
        try {
            for (SOBIMetadata metaData : metaDataList) {
                SOBIFile sobiFile = new SOBIFile(getSOBIFileFromFileSystem(metaData));
                metaData.applyToSOBIFile(sobiFile);
                sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }
        catch (IOException ex) {
            logger.error("Failed to construct SOBIFile instance!", ex);
            return null;
        }
        return sobiFileMap;
    }

    /** {@inheritDoc }*/
    @Override
    public List<SOBIFile> getSOBIFilesDuring(Date start, Date end, boolean processedOnly, SortOrder sortByPubDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", start);
        params.addValue("endDate", end);
        params.addValue("processedOnly", processedOnly);
        List<SOBIMetadata> metaDataList = jdbcNamed.query(GET_SOBI_FILES_DURING_SQL + orderBy("published_date_time", sortByPubDate),
                                                          params, new SOBIMetadata.Mapper());
        return getSobiFileListFromMetaDataList(metaDataList);
    }

    /** {@inheritDoc }*/
    @Override
    public List<SOBIFile> getPendingSOBIFiles(SortOrder sortByPubDate) {
        List<SOBIMetadata> metaDataList =
            jdbc.query(GET_PENDING_SOBI_FILES_SQL + orderBy("published_date_time", sortByPubDate), new SOBIMetadata.Mapper());
        return getSobiFileListFromMetaDataList(metaDataList);
    }

    /** {@inheritDoc }*/
    @Override
    public void stageSOBIFiles(boolean allowReStaging) throws IOException {
        logger.info("Performing staging of new SOBI files...");
        int filesStaged = 0;

        for (File file : safeListFiles(this.stagingSobiDir, null, false)) {
            SOBIFile sobiFile = new SOBIFile(file);
            sobiFile.setPendingProcessing(true);
            try {
                insertSOBIMetadataInDatabase(sobiFile);
                if (!file.delete()) {
                    logger.fatal("Failed to delete " + file.getAbsoluteFile() + " from the sobi staging directory!");
                }
                updateSOBIFileInFileSystem(sobiFile);
                filesStaged++;
                logger.debug("Staged " + sobiFile);
            }
            catch(DataIntegrityViolationException ex) {
                if (allowReStaging) {
                    // The SOBI file has been staged once before so we want to re-stage it with the contents of the
                    // new file which may or may not be identical. This should rarely occur in production but can
                    // be helpful during development.
                    SOBIFile existingSOBIFile = getSOBIFile(sobiFile.getFileName());
                    if (existingSOBIFile != null) {
                        File existingFile = getSOBIFileFromFileSystem(existingSOBIFile);
                        if (existingFile.exists()) {
                            moveFileToDirectory(file, this.workingSobiDir, true);
                            existingSOBIFile.setPendingProcessing(true);
                            updateSOBIFile(existingSOBIFile);
                            filesStaged++;
                            logger.debug("Re-staged " + existingSOBIFile + " with latest file contents.");
                        }
                        else {
                            logger.warn("Attempted to re-stage " + existingSOBIFile + " but the file " +
                                    existingFile.getAbsolutePath() + " doesn't exist!");
                        }
                    }
                    else {
                        logger.warn("Failed to stage " + sobiFile + " because a prior record exists but cannot be retrieved!");
                    }
                }
                else {
                    logger.debug("Skipping " + sobiFile + " since re-staging is disabled.");
                }
            }
        }
        logger.info("Staging of " + filesStaged + " SOBI files completed.");
    }

    /** {@inheritDoc }*/
    @Override
    public boolean updateSOBIFile(SOBIFile sobiFile) {
        if (sobiFile != null) {
            try {
                int affected = jdbcNamed.update(UPDATE_SOBI_FILE, getSOBIInsertUpdateParams(sobiFile));
                if (affected > 0) {
                    updateSOBIFileInFileSystem(sobiFile);
                    return true;
                }
                return false;
            }
            catch (DataAccessException ex) {
                logger.error("Failed to update SOBIFile record with file name " + sobiFile.getFileName(), ex);
            }
            catch (IOException ex) {
                logger.error("Failed to update SOBI file " + sobiFile.getFileName() + " in file system!", ex);
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Supplied SOBIFile object is null. Cannot persist to database.");
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

    /** Models the processing details for a sobi file which is stored in the database */
    protected static class SOBIMetadata {
        String fileName;
        Date publishedDateTime;
        Date processedDateTime;
        boolean pendingProcessing;
        int processedCount;

        /** Apply metadata info to SOBIFile */
        void applyToSOBIFile(SOBIFile sobiFile) {
            sobiFile.setProcessedDateTime(this.processedDateTime);
            sobiFile.setPendingProcessing(this.pendingProcessing);
            sobiFile.setProcessedCount(this.processedCount);
        }

        /** Row Mapper implementation */
        static class Mapper implements RowMapper<SOBIMetadata> {
            @Override
            public SOBIMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
                SOBIMetadata metadata = new SOBIMetadata();
                metadata.fileName = rs.getString("file_name");
                metadata.publishedDateTime = rs.getDate("published_date_time");
                metadata.processedDateTime = rs.getDate("processed_date_time");
                metadata.pendingProcessing = rs.getBoolean("pending_processing");
                metadata.processedCount = rs.getInt("processed_count");
                return metadata;
            }
        }
    }

    /**
     * Saves the metadata of the SOBIFile into the database.
     * @throws java.lang.IllegalArgumentException if sobiFile is null
     */
    private void insertSOBIMetadataInDatabase(SOBIFile sobiFile) throws DataAccessException {
        if (sobiFile != null) {
            jdbcNamed.update(INSERT_SOBI_FILE, getSOBIInsertUpdateParams(sobiFile));
        }
        else {
            throw new IllegalArgumentException("Supplied SOBIFile object is null. Cannot persist to database.");
        }
    }

    /**
     * Fetch actual SOBI file from the file system. It will either be in the working or archive
     * directory based on the pendingProcessing flag.
     * @param fileName String - File name of the SOBI file.
     * @param pendingProcessing boolean - Indicates where to look for the SOBI file.
     * @return File
     */
    private File getSOBIFileFromFileSystem(String fileName, Date publishedDate, boolean pendingProcessing) {
        return (pendingProcessing) ? getFileInWorkingDirectory(fileName) : getFileInArchiveDirectory(fileName, publishedDate);
    }

    /** Overload for convenience. @see #getSOBIFileFromFileSystem(String, java.util.Date, boolean) */
    private File getSOBIFileFromFileSystem(SOBIMetadata metaData) {
        return getSOBIFileFromFileSystem(metaData.fileName, metaData.publishedDateTime, metaData.pendingProcessing);
    }

    /** Overload for convenience. @see #getSOBIFileFromFileSystem(String, java.util.Date, boolean) */
    private File getSOBIFileFromFileSystem(SOBIFile sobiFile) {
        return getSOBIFileFromFileSystem(sobiFile.getFileName(), sobiFile.getPublishedDateTime(), sobiFile.isPendingProcessing());
    }

    /**
     * Deletes the actual SOBI file from the file system and writes the contents of SOBIFile to the appropriate directory.
     * @param sobiFile SOBIFile
     * @return File - Newly written file.
     * @throws IOException
     */
    private File updateSOBIFileInFileSystem(SOBIFile sobiFile) throws IOException {
        File workFile = getFileInWorkingDirectory(sobiFile.getFileName());
        File archiveFile = getFileInArchiveDirectory(sobiFile.getFileName(), sobiFile.getPublishedDateTime());

        // Delete the file from any possible directory so we don't have to keep track of where it is.
        FileUtils.deleteQuietly(workFile);
        FileUtils.deleteQuietly(archiveFile);

        if (sobiFile.isPendingProcessing()) {
            // Write to the working directory.
            FileUtils.write(workFile, sobiFile.getText());
            return workFile;
        }
        else {
            // Create any sub directories just to be safe and write to archive directory.
            archiveFile.getParentFile().mkdirs();
            FileUtils.write(archiveFile, sobiFile.getText());
            return archiveFile;
        }
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
     * @param publishedDate Date
     * @return File
     */
    private File getFileInArchiveDirectory(String fileName, Date publishedDate) {
        LocalDate localDate = new LocalDate(publishedDate);
        String year = Integer.toString(localDate.getYear());
        File dir = new File(this.archiveSobiDir, year);
        return new File(dir, fileName);
    }

    /**
     * Convenience method to convert a list of SOBIMetaData objects into SOBIFile objects.
     * @param metaDataList List<SOBIMetaData>
     * @return List<SOBIFile>
     */
    private List<SOBIFile> getSobiFileListFromMetaDataList(List<SOBIMetadata> metaDataList) {
        List<SOBIFile> sobiFiles = new ArrayList<>();
        try {
            for (SOBIMetadata metaData : metaDataList) {
                SOBIFile sobiFile = new SOBIFile(getSOBIFileFromFileSystem(metaData));
                metaData.applyToSOBIFile(sobiFile);
                sobiFiles.add(sobiFile);
            }
        }
        catch (IOException ex) {
            logger.error("Failed to construct SOBIFile instance!", ex);
            return null;
        }
        return sobiFiles;
    }

    /**
     * Returns a ParameterSource with columns mapped to SOBIFile values for use in update/insert queries.
     * @param sobiFile SOBIFile
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource getSOBIInsertUpdateParams(SOBIFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sobiFile.getFileName());
        params.addValue("publishedDateTime", toTimestamp(sobiFile.getPublishedDateTime()));
        params.addValue("processedDateTime", toTimestamp(sobiFile.getProcessedDateTime()));
        params.addValue("processedCount", sobiFile.getProcessedCount());
        params.addValue("pendingProcessing", sobiFile.isPendingProcessing());
        return params;
    }
}
