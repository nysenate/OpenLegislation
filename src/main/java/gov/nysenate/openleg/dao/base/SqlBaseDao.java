package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Base class for SQL data access layer classes to inherit common functionality from.
 */
public abstract class SqlBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBaseDao.class);

    /** JdbcTemplate reference for use by sub classes to execute SQL queries */
    @Autowired
    protected JdbcTemplate jdbc;

    /** Similar to JdbcTemplate but forces the use of named query parameter for readability. */
    @Autowired
    protected NamedParameterJdbcTemplate jdbcNamed;

    /** Reference to the environment in which the data is stored */
    @Autowired
    protected Environment environment;

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
        obj.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
        obj.setPublishedDateTime(getLocalDateTime(rs.getTimestamp("published_date_time")));
    }

    /** --- Static Helper Methods --- */

    /**
     * Convert a LocalDateTime to a Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a LocalDate to a Date.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return toDate(localDate.atStartOfDay());
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone.
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDateTime.
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        return getLocalDateTime(rs.getTimestamp(column));
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone.
     */
    public static LocalDate getLocalDate(Date date) {
        if (date == null) return null;
        return getLocalDateTime(date).toLocalDate();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDate.
     */
    public static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        return rs.getDate(column).toLocalDate();
    }

    /**
     * Read the 'column' int value from the result set and return a new SessionYear instance.
     */
    public static SessionYear getSessionYear(ResultSet rs, String column) throws SQLException {
        return new SessionYear(rs.getInt(column));
    }
}