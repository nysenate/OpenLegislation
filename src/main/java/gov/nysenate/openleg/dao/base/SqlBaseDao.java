package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
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
    protected static void addModPubDateParams(Date modifiedDate, Date publishedDate, MapSqlParameterSource params) {
        params.addValue("modifiedDateTime", modifiedDate);
        params.addValue("publishedDateTime", publishedDate);
    }

    /** --- Static Helper Methods --- */

    /**
     * Converts Date to Timestamp since the conversion may not be implicit with all database libraries.
     */
    protected static Timestamp toTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }
}
