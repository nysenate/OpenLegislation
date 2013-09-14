package gov.nysenate.openleg.api.servlets.admin;

import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.Storage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class UpdateServlet
 */
@SuppressWarnings("serial")
public class UpdatesServlet extends HttpServlet
{
    public static final int QUERY_LIMIT = 250;

    private static class ChangeHandler implements ResultSetHandler<ArrayList<Change>> {

        @Override
        public ArrayList<Change> handle(ResultSet results) throws SQLException
        {
            ArrayList<Change> changes = new ArrayList<Change>();
            while(results.next()) {
                    try {
                        changes.add(new Change(
                            results.getString("oid"),
                            results.getString("otype"),
                            Storage.Status.valueOf(results.getString("status")),
                            mysqlDateFormat.parse(results.getString("time"))
                        ));
                    }
                    catch (ParseException e) {
                        logger.error("Invalid change.time format", e);
                    }
            }
            return changes;
        }
    }

    private static final Logger logger = Logger.getLogger(UpdatesServlet.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final QueryRunner runner;

    private final ResultSetHandler<ArrayList<Change>> handler;

    private final List<String> otypes = Arrays.asList("bill","calendar","meeting","agenda");

    public UpdatesServlet()
    {
        super();
        handler = new ChangeHandler();
        runner = new QueryRunner(Application.getDB().getDataSource());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    protected String getSafe(HttpServletRequest request, String key, String safe) {
        String value = request.getParameter(key);
        if (value == null) {
            return safe;
        }
        else {
            return value;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String start = getSafe(request, "start", "");
        String end = getSafe(request, "end", "");
        String otype = getSafe(request, "otype", "");
        String oid = getSafe(request, "oid", "");

        Date startDate = null;
        Date endDate = null;

        try {
            if (!start.isEmpty()) {
                startDate = dateFormat.parse(start+" 00:00:00");
                request.setAttribute("startDate", startDate);
            }

            if (!start.isEmpty()) {
                endDate = dateFormat.parse(end+" 23:59:59");
                request.setAttribute("endDate", endDate);
            }

            otype = otype.toLowerCase();
            if (!otypes.contains(otype)) {
                // Alert user to malformed type field
            }

            List<Change> changes = getHistory(startDate, endDate, otype, oid);
            if (changes.size() == QUERY_LIMIT) {
                request.setAttribute("warning", "Result set limited to "+QUERY_LIMIT+" results. Please narrow your query.");
            }
            TreeMap<Date, TreeMap<Date, ArrayList<Change>>> structuredChanges = structureChanges(changes);
            request.setAttribute("changes", structuredChanges);
        }
        catch (ParseException e) {
            // Alert the user to malformed date field
            logger.error(e,e);
            request.setAttribute("warning", "Unexpected Exception: "+e.getMessage());
        }
        catch (SQLException e) {
            // Alert the user to the streams
            logger.error(e,e);
            request.setAttribute("warning", "Unexpected Exception: "+e.getMessage());
        }

        request.getRequestDispatcher("/admin/updates.jsp").forward(request, response);
    }

    /**
     * Construct a list of Updates from the database using the given query parameters. All parameters
     * are optional where a null or empty value excludes it from the filter.
     *
     * @param start - the fully qualified date & time to start getting updates from.
     * @param end - the fully qualified date & time to stop getting updates at.
     * @param otype - the otype of the document types to get updates for.
     * @param oid - the oid of the document to get updates for.
     * @return List of matching Updates
     * @throws SQLException
     */
    public List<Change> getHistory(Date start, Date end, String otype, String oid) throws SQLException
    {

        String query = "SELECT * FROM changelog WHERE 1=1";
        List<Object> params = new ArrayList<Object>();

        if (start != null) {
            query += " AND time >= ?";
            params.add(mysqlDateFormat.format(start));
        }

        if (end != null) {
            query += " AND time <= ?";
            params.add(mysqlDateFormat.format(end));
        }

        if (otype != null && !otype.isEmpty()) {
            query += " AND otype = ?";
            params.add(otype);
        }

        if (oid != null && !oid.isEmpty()) {
            query += " AND oid = ?";
            params.add(oid);
        }

        query += " ORDER BY time desc LIMIT "+QUERY_LIMIT;
        logger.info(query);
        logger.info(params);
        return runner.query(query, handler, params.toArray());
    }

    private TreeMap<Date, TreeMap<Date, ArrayList<Change>>> structureChanges(List<Change> changes) {
        Calendar cal = Calendar.getInstance();
        TreeMap<Date, TreeMap<Date, ArrayList<Change>>> structuredChanges = new TreeMap<Date, TreeMap<Date, ArrayList<Change>>>(Collections.reverseOrder());

        // Step through in chronological order
        Collections.sort(changes);

        Date currentDate = null;
        Date currentTime = null;

        ArrayList<Change> timeChanges = null;
        TreeMap<Date, ArrayList<Change>> dayChanges = null;
        for (Change change : changes) {
            Date time = change.getTime();
            cal.setTime(time);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();

            if (currentDate == null || !currentDate.equals(date)) {
                // New Date!
                dayChanges = new TreeMap<Date, ArrayList<Change>>(Collections.reverseOrder());
                structuredChanges.put(date, dayChanges);
                currentDate = date;
            }

            if (currentTime == null || !currentTime.equals(time)) {
                // New Time!
                timeChanges = new ArrayList<Change>();
                dayChanges.put(time, timeChanges);
                currentTime = time;
            }

            timeChanges.add(change);
        }

        return structuredChanges;
    }
}
