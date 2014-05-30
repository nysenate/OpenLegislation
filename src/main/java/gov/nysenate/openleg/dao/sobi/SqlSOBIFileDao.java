package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 */
public class SqlSOBIFileDao extends SqlBaseDao implements SOBIFileDao
{
    private static Logger logger = Logger.getLogger(SqlSOBIFileDao.class);

    /** The database table where the sobi files are recorded */
    private static String SOBI_TABLE = "sobi";

    public SqlSOBIFileDao(Environment environment) {
        this.environment = environment;
    }

    /** --- SQL Statements --- */

    private final String GET_SOBI_INFO_BY_FILENAME_SQL =
        "SELECT * FROM " + table(SOBI_TABLE) + " WHERE file_name = :fileName";

    private final String INSERT_SOBI_INFO =
        "INSERT INTO " + table(SOBI_TABLE) + " (file_name, published_date_time, processed_date_time, processed_count, pending_processing) " +
        "VALUES (:fileName, :publishedDateTime, :processedDateTime, :processedCount, :pendingProcessing)";

    private final String UPDATE_SOBI_INFO =
        "UPDATE " + table(SOBI_TABLE) + "\n" +
        "SET published_date_time = :publishedDateTime," +
        "    processed_date_time = :processedDateTime," +
        "    pending_processing = :pendingProcessing," +
        "    processed_count = :processedCount) " +
        "WHERE file_name = :fileName";

    /** --- Helper Classes --- */

    /** Models the processing details for a sobi file which is stored in the database */
    protected static class SOBIDetails {
        Date publishedDateTime;
        Date processedDateTime;
        boolean pendingProcessing;
        int processedCount;

        /** Row Mapper implementation */
        static class Mapper implements RowMapper<SOBIDetails> {
            @Override
            public SOBIDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                SOBIDetails details = new SOBIDetails();
                details.processedDateTime = rs.getDate("processed_date_time");
                details.pendingProcessing = rs.getBoolean("pending_processing");
                details.processedCount = rs.getInt("processed_count");
                return details;
            }
        }
    }

    /** --- Implemented Methods --- */

    /**
     * {@inheritDoc}
     *
     * SOBI files are stored in the file system to preserve their original formatting whereas metadata
     * for the files is stored in the database. The returned SOBIFile instances are constructed utilizing both
     * data sources.
     */
    public SOBIFile getSOBI(String fileName, File directory) {
        SOBIFile sobi = null;
        File sobiFile = new File(directory, fileName);
        if (sobiFile.exists()) {
            try {
                sobi = new SOBIFile(sobiFile);
                MapSqlParameterSource params = new MapSqlParameterSource("fileName", fileName);
                try {
                    SOBIDetails details = jdbcNamed.queryForObject(GET_SOBI_INFO_BY_FILENAME_SQL, params, new SOBIDetails.Mapper());
                    sobi.setPublishedDateTime(details.publishedDateTime);
                    sobi.setProcessedDateTime(details.processedDateTime);
                    sobi.setPendingProcessing(details.pendingProcessing);
                    sobi.setProcessedCount(details.processedCount);
                }
                catch (IncorrectResultSizeDataAccessException ex) {
                    logger.debug("No sobi details were found for " + fileName);
                }
                return sobi;
            }
            catch (Exception ex) {
                logger.error("Failed to construct SOBIFile instance from file " + directory.getAbsolutePath() + fileName, ex);
            }
        }
        return sobi;
    }

    /**
     * {@inheritDoc}
     *
     * Saves a record of the SOBIFile into the database and writes it to file in the specified directory.
     * @throws java.lang.IllegalArgumentException if sobiFile is null
     */
    public boolean insertSOBI(SOBIFile sobiFile, File directory) {
        if (sobiFile != null) {
            try {
                int affected = jdbcNamed.update(INSERT_SOBI_INFO, getSOBIInsertUpdateParams(sobiFile));
                return (affected > 0);
            }
            catch (DataIntegrityViolationException ex) {
                logger.warn("Record with the SOBIFile filename " + sobiFile.getFileName() + " already exists in the table. " +
                            "Consider using the respective update method instead.", ex);
            }
            catch (DataAccessException ex) {
                logger.error("Failed to insert SOBIFile record with file name " + sobiFile.getFileName(), ex);
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Supplied SOBIFile object is null. Cannot persist to database.");
        }
    }

    public boolean updateSOBI(SOBIFile sobiFile, File directory) {
        if (sobiFile != null) {
            try {
                int affected = jdbcNamed.update(UPDATE_SOBI_INFO, getSOBIInsertUpdateParams(sobiFile));
                return (affected > 0);
            }
            catch (DataAccessException ex) {
                logger.error("Failed to update SOBIFile record with file name " + sobiFile.getFileName(), ex);
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Supplied SOBIFile object is null. Cannot persist to database.");
        }
    }

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
