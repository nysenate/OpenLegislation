package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.util.Application;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

    /** --- Table Name Definitions --- */

    protected static String BILL_TABLE = "bill";
    protected static String BILL_AMENDEMENT_TABLE = "bill_amendment";
    protected static String BILL_AMENDMENT_ACTION_TABLE = "bill_amendment_action";
    protected static String BILL_AMENDMENT_COSPONSOR_TABLE = "bill_amendment_cosponsor";
    protected static String BILL_AMENDMENT_VOTE = "bill_amendment_vote";

    protected SqlBaseDao(Environment environment) {
        this.environment = environment;
    }

    /**
     * Returns the schema of the environment instance.
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
     * @param tableName String
     * @return String
     */
    protected String table(String tableName) {
        return schema() + "." + tableName;
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

    /**
     * Outputs the ORDER BY clause for multiple column name and sort order pairs.
     * @param columnNames List<String>
     * @param sortOrders List<SortOrder>>
     * @return String
     */
    protected String orderBy(List<String> columnNames, List<SortOrder> sortOrders) {
        if (columnNames.size() != sortOrders.size()) {
            throw new IllegalArgumentException("For order by clause, number of column names must match sort orders!");
        }
        String orderByClause = "ORDER BY ";
        for (int i = 0; i < columnNames.size(); i++) {
            if (sortOrders.get(i) != null && !sortOrders.get(i).equals(SortOrder.NONE)) {
                orderByClause += (columnNames.get(i) + " " + sortOrders.get(i).name() + ((i + 1 == columnNames.size()) ? " " : ", "));
            }
        }
        return orderByClause;
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
