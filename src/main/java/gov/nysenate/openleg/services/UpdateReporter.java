package gov.nysenate.openleg.services;

import gov.nysenate.openleg.model.Update;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.Storage.Status;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/*
 * Parses changes from a changeLog file and saves to MySQL database.
 */
public class UpdateReporter extends ServiceBase
{
    @Override
    public boolean process(HashMap<String, Storage.Status> changeLog, Storage storage) throws IOException
    {
        ArrayList<Update> updates = new ArrayList<Update>();
        // Parse all changes in log file.
        for(Entry<String, Storage.Status> change: changeLog.entrySet())
        {
            String key = change.getKey();
            Status status = change.getValue();
            String otype = key.split("/")[1];
            String oid = key.split("/")[2];

            // Create a bean object for this update.
            Update update = new Update();
            update.setOid(oid);
            update.setOtype(otype);
            update.setStatus(status.toString());

            // Create date string in mySQL format for the current time.
            Date date = new Date();
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(date);
            update.setDate(currentTime);
            updates.add(update);
        }
        insertUpdates(updates);
        return true;
    }

    public void insertUpdates(List<Update> updates)
    {
        // DataSource settings.
        String server = "localhost";
        String port = "3306";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "openleg";
        String password = "openleg";
        String url = "jdbc:mysql://" + server +":" + port + "/";
        BasicDataSource datasource = new BasicDataSource();
        datasource.setDriverClassName(driver);
        datasource.setUrl(url);
        datasource.setUsername(userName);
        datasource.setPassword(password);

        // Insert changes into database.
        QueryRunner run = new QueryRunner(datasource);
        try {
            for(Update update: updates){
                run.update("INSERT INTO OpenLegUpdateTracker.Updates(otype, oid, date, status) values(?, ?, ?, ?)",
                        update.getOtype(), update.getOid(), update.getDate(), update.getStatus());
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
