package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.model.admin.Update;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;

import java.sql.SQLException;
import java.util.ArrayList;
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
        ArrayList<Update> updates = new ArrayList<Update>();
        // Parse all changes in log file.
        for(Entry<String, Change> changeEntry: entries)
        {
            String key = changeEntry.getKey();
            String otype = key.split("/")[1];
            String oid = key.split("/")[2];
            Change change = changeEntry.getValue();

            // Create a bean object for this update.
            Update update = new Update();
            update.setOid(oid);
            update.setOtype(otype);
            update.setStatus(change.getStatus().toString());
            // Format the Date for MySql query.
            update.setTime(change.getDate());
            updates.add(update);
        }
        insertUpdates(updates);
        return true;
    }

    private static void insertUpdates(List<Update> updates)
    {
        // Insert changes into database.
        DataSource datasource = Application.getDB().getDataSource();
        QueryRunner run = new QueryRunner(datasource);
        try {
            run.update("BEGIN");
            for(Update update: updates){
                run.update("INSERT INTO changelog (otype, oid, time, status) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE oid = ?",
                        update.getOtype(), update.getOid(), update.getTime(), update.getStatus(), update.getOid());
            }
            run.update("COMMIT");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
