package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.Update;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Change;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.jdbc.pool.DataSource;

/*
 * Parses changes from a changeLog file and saves to MySQL database.
 */
public class UpdateReporter
{
    public static void process(HashMap<String, Change> changeLog)
    {
        ArrayList<Update> updates = new ArrayList<Update>();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Parse all changes in log file.
        for(Entry<String, Change> changeEntry: changeLog.entrySet())
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
            update.setDate(sdf.format(change.getDate()));
            updates.add(update);
        }
        insertUpdates(updates);
    }

    private static void insertUpdates(List<Update> updates)
    {
        // Insert changes into database.
        DataSource datasource = Application.getDB().getDataSource();
        QueryRunner run = new QueryRunner(datasource);
        try {
            for(Update update: updates){
                run.update("INSERT INTO updates(otype, oid, date, status) values(?, ?, ?, ?)",
                        update.getOtype(), update.getOid(), update.getDate(), update.getStatus());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
