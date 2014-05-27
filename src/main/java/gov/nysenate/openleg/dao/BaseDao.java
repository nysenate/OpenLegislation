package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.util.Application;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Timestamp;
import java.util.Date;

public class BaseDao
{
    protected QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());

    protected static String defaultEnvSchema = "master";

    protected static Timestamp toTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }
}
