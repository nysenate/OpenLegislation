package gov.nysenate.openleg.dao.base;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.util.Application;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Base class for SQL data access layer classes to inherit common functionality from.
 */
public abstract class SqlBaseDao
{
    protected static String DEFAULT_ENV_SCHEMA = Environment.DEFAULT_SCHEMA;

    /** QueryRunner reference for use by sub classes to execute SQL queries */
    protected QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

    /** Reference to the environment in which the data is stored */
    protected Environment environment;

    protected static Timestamp toTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }
}
