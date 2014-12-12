package gov.nysenate.openleg.dao.base;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.util.DateUtils.toDate;

/**
 * Base class for SQL data access layer classes to inherit common functionality from.
 */
public abstract class SqlBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBaseDao.class);

    /** JdbcTemplate reference for use by sub classes to execute SQL queries */
    @Autowired protected JdbcTemplate jdbc;

    /** Similar to JdbcTemplate but forces the use of named query parameter for readability. */
    @Autowired protected NamedParameterJdbcTemplate jdbcNamed;

    /** Reference to the environment in which the data is stored */
    @Autowired protected Environment environment;

    @PostConstruct
    private void init() {}

    /** --- Common Param Methods --- */

    /**
     * Returns the schema of the environment instance.
     */
    protected String schema() {
        if (environment == null) {
            throw new IllegalStateException("The environment has not been initialized. Cannot perform SQL queries " +
                    "since we can't determine which database schema to operate on.");
        }
        return environment.getSchema();
    }

    /**
     * Applies the 'last SobiFragment id' column value. Useful for tracking which sobiFragment
     * serves as the source data for the update.
     */
    protected static void addLastFragmentParam(SobiFragment fragment, MapSqlParameterSource params) {
        params.addValue("lastFragmentId", (fragment != null) ? fragment.getFragmentId() : null);
    }

    /**
     * Applies the published date / modified date column values.
     */
    protected static void addModPubDateParams(LocalDateTime modifiedDate, LocalDateTime publishedDate, MapSqlParameterSource params) {
        params.addValue("modifiedDateTime", toDate(modifiedDate));
        params.addValue("publishedDateTime", toDate(publishedDate));
    }

    /**
     * Convenience method for setting the modified and published date time via the default columns
     * in the result set. Use this method only when the result set is guaranteed to have these
     * default columns.
     */
    protected static void setModPubDatesFromResultSet(BaseLegislativeContent obj, ResultSet rs) throws SQLException {
        obj.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        obj.setPublishedDateTime(DateUtils.getLocalDateTime(rs.getTimestamp("published_date_time")));
    }

    /** --- File Handling Methods --- */

    /**
     * Moves the file into the destination quietly.
     */
    protected void moveFile(File sourceFile, File destFile) throws IOException {
        if (destFile.exists()) {
            FileUtils.deleteQuietly(destFile);
        }
        FileUtils.moveFile(sourceFile, destFile);
    }

    /** --- PostgreSQL Hstore handling methods --- */

    /**
     * Converts the output of hstore_to_array(column) to a mapping of the hstore key/val pairs.
     * For example if you have an hstore value 'a=>1, b=>2', to retrieve a Map {a=1, b=2} have the
     * sql query return hstore_to_array(column) and feed the result set to this method.
     */
    public static Map<String, String> getHstoreMap(ResultSet rs, String column) throws SQLException {
        String[] hstoreArr = (String[]) rs.getArray(column).getArray();
        Map<String, String> hstoreMap = new HashMap<>();
        String key = "";
        for (int i = 0; i < hstoreArr.length; i++) {
            if (i % 2 == 0) {
                key = hstoreArr[i];
            }
            else {
                hstoreMap.put(key, hstoreArr[i]);
            }
        }
        return hstoreMap;
    }

    /**
     * Converts the given map into the hstore string format (i.e. 'key1=>val1, key2=>val2, etc')
     */
    public static String toHstoreString(Map<String, String> hstoreMap) {
        return hstoreMap.entrySet().stream()
            .map(kv -> kv.getKey() + "=>" + kv.getValue())
            .collect(Collectors.joining(","));
    }

    /** --- Date Methods -- */

    /**
     * Given a sobi fragment id, parse out the date/time. Returns null if the fragment id has a different pattern
     * than usual..
     *
     * @param fragmentId String
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTimeFromSobiFragmentId(String fragmentId) {
        if (fragmentId != null && !fragmentId.isEmpty()) {
            List<String> parts = Splitter.on(".").splitToList(fragmentId);
            if (parts.size() == 4) {
                try {
                    return LocalDateTime.parse(parts.get(1).substring(1) + parts.get(2).substring(1),
                            DateTimeFormatter.ofPattern("yyMMddHHmmss"));
                }
                catch (DateTimeParseException ex) {
                    logger.warn("Failed to parse date time from sobi fragment {}", fragmentId, ex);
                }
            }
        }
        return null;
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDateTime.
     * Return null if the column value is null.
     */
    public static LocalDateTime getLocalDateTimeFromRs(ResultSet rs, String column) throws SQLException {
        if (rs.getTimestamp(column) == null) return null;
        return rs.getTimestamp(column).toLocalDateTime();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDate.
     * Return null if the column value is null.
     */
    public static LocalDate getLocalDateFromRs(ResultSet rs, String column) throws SQLException {
        if (rs.getDate(column) == null) return null;
        return rs.getDate(column).toLocalDate();
    }

    /**
     * Read the 'column' time value from the result set and cast it to a LocalTime.
     * Return null if the column value is null.
     */
    public static LocalTime getLocalTimeFromRs(ResultSet rs, String column) throws SQLException {
        if (rs.getTime(column) == null) return null;
        return rs.getTime(column).toLocalTime();
    }

    /**
     * Read the 'column' int value from the result set and return a new SessionYear instance.
     */
    public static SessionYear getSessionYearFromRs(ResultSet rs, String column) throws SQLException {
        return new SessionYear(rs.getInt(column));
    }
}