package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.jdbc.pool.DataSource;

/*
 * Parses changes from a changeLog file and saves to MySQL database.
 */
public class UpdateReporter extends ServiceBase
{
    public boolean process(List<Entry<String, Change>> entries, Storage storage)
    {
        // Insert changes into database.
        DataSource datasource = Application.getDB().getDataSource();
        QueryRunner run = new QueryRunner(datasource);
        try {
            run.update("BEGIN");
            for(Entry<String, Change> entry: entries) {
                Change change = entry.getValue();
                run.update("INSERT INTO changelog (otype, oid, time, status) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE oid = ?",
                        change.getOtype(), change.getOid(), change.getTime(), change.getStatus().name(), change.getOid());
            }
            run.update("COMMIT");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
