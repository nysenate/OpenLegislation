package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.util.Application;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Base class for SQL data access layer classes to inherit common functionality from.
 */
public abstract class SqlBaseDao
{
    protected static String DEFAULT_ENV_SCHEMA = Environment.DEFAULT_SCHEMA;

    /** JdbcTemplate reference for use by sub classes to execute SQL queries */
    protected JdbcTemplate jdbc = new JdbcTemplate(Application.getDB().getDataSource());

    /** Similar to JdbcTemplate but forces the use of named query parameter.
     *  Can aid in readability for complex queries. */
    protected NamedParameterJdbcTemplate jdbcNamed = new NamedParameterJdbcTemplate(Application.getDB().getDataSource());

    /** Reference to the environment in which the data is stored */
    protected Environment environment;

    protected SqlBaseDao(Environment environment) {
        this.environment = environment;
    }

    /**
     * Returns the schema of the environment instance.
     * @return String
     */
    protected String getEnvSchema() {
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
     * @param tableName String
     * @return String
     */
    protected String table(String tableName) {
        return getEnvSchema() + "." + tableName;
    }

    /**
     * Outputs the ORDER BY clause for the given column name and sort order.
     * @param columnName String
     * @param sortOrder SortOrder
     * @return String
     */
    protected String orderBy(String columnName, SortOrder sortOrder) {
        if (sortOrder == null || sortOrder.equals(SortOrder.NONE)) {
            return "";
        }
        return "ORDER BY " + columnName  + " " + sortOrder.name();
    }

    /** --- Static Helper Methods --- */

    /**
     * Converts Date to Timestamp since the conversion may not be implicit with all database libraries.
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
