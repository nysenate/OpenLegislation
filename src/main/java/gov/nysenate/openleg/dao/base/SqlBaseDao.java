package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.app.EnvironmentDao;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.util.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Base class for SQL data access layer classes to inherit common functionality from.
 */
public abstract class SqlBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlBaseDao.class);

    protected static String DEFAULT_ENV_SCHEMA = Environment.DEFAULT_SCHEMA;

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
    private void init() {
        logger.info("Environment autowired: " + environment);
    }

    /** --- Common Param Methods --- */

    /**
     * Applies the 'last SobiFragment id' column value. Useful for tracking which sobiFragment
     * serves as the source data for the update.
     */
    protected static void addSobiFragmentParams(SobiFragment fragment, MapSqlParameterSource params) {
        params.addValue("lastFragmentId", (fragment != null) ? fragment.getFragmentId() : null);
    }

    /**
     * Applies the published date / modified date column values.
     */
    protected static void addModPubDateParams(Date modifiedDate, Date publishedDate, MapSqlParameterSource params) {
        params.addValue("modifiedDateTime", modifiedDate);
        params.addValue("publishedDateTime", publishedDate);
    }

    /**
     * Returns the schema of the environment instance.
     *
     * @return String
     */
    protected String schema() {
        if (environment == null) {
            throw new IllegalStateException("The environment has not been initialized. Cannot perform SQL queries " +
                                            "since we can't determine which database schema to operate on.");
        }
        return environment.getSchema();
    }

    /**
     * For the given tableName this method will return a string with the current environment schema
     * prefixed. For example if current env schema is master and 'tableName' is test, the string
     * 'master.test' will be returned.
     *
     * @param tableName String
     * @return String
     */
    protected String table(String tableName) {
        return schema() + "." + tableName;
    }

    /**
     * Outputs the ORDER BY clause for the given column name and sort order.
     *
     * @param columnName String
     * @param sortOrder SortOrder
     * @return String
     */
    protected static String orderBy(String columnName, SortOrder sortOrder) {
        if (sortOrder == null || sortOrder.equals(SortOrder.NONE)) {
            return "";
        }
        return " ORDER BY " + columnName  + " " + sortOrder.name();
    }

    /**
     * Outputs the ORDER BY clause for multiple column name and sort order pairs.
     *
     * @param columnNames List<String>
     * @param sortOrders List<SortOrder>>
     * @return String
     */
    protected static String orderBy(List<String> columnNames, List<SortOrder> sortOrders) {
        if (columnNames.size() != sortOrders.size()) {
            throw new IllegalArgumentException("For order by clause, number of column names must match sort orders!");
        }
        String orderByClause = " ORDER BY ";
        for (int i = 0; i < columnNames.size(); i++) {
            if (sortOrders.get(i) != null && !sortOrders.get(i).equals(SortOrder.NONE)) {
                orderByClause += (columnNames.get(i) + " " + sortOrders.get(i).name() + ((i + 1 == columnNames.size()) ? " " : ", "));
            }
        }
        return orderByClause;
    }

    /**
     * Outputs the LIMIT clause if 'limit' > 0.
     *
     * @param limit int
     * @return String
     */
    protected static String limit(int limit) {
        return (limit > 0) ? " LIMIT " + limit : "";
    }

    /**
     * Outputs the LIMIT OFFSET clause if 'limit' > 0.
     *
     * @param limit int
     * @return String
     */
    protected static String limitOffset(int limit, int offset) {
        return (limit > 0) ? limit(limit) + " OFFSET " + offset : "";
    }

    /** --- Static Helper Methods --- */

    /**
     * Converts Date to Timestamp since the conversion may not be implicit with all database libraries.
     *
     * @param date Date
     * @return Timestamp
     */
    protected static Timestamp toTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }
}
