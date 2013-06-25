package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.model.Update;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class UpdateServlet
 */
public class UpdateServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private final Logger logger = Logger.getLogger(UpdateServlet.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private final SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final QueryRunner runner;

    private final ResultSetHandler<List<Update>> handler;

    private final List<String> otypes = Arrays.asList("bill","calendar","meeting","agenda");

    public UpdateServlet()
    {
        super();
        handler = new BeanListHandler<Update>(Update.class);
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
            }

            if (!start.isEmpty()) {
                endDate = dateFormat.parse(end+" 23:59:59");
            }

            otype = otype.toLowerCase();
            if (!otypes.contains(otype)) {
                // Alert user to malformed type field
            }

            if (start.isEmpty() && end.isEmpty() && otype.isEmpty() && oid.isEmpty()) {
                // If no parameters were supplied, add some defaults
                Calendar now = Calendar.getInstance();
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.set(Calendar.HOUR, 0);
                now.set(Calendar.MINUTE, 0);
                now.set(Calendar.SECOND, 0);
                now.set(Calendar.MILLISECOND, 0);

                endDate = DateUtils.addDays(now.getTime(), 1);
                startDate = DateUtils.addDays(now.getTime(), -3);
            }

            List<Update> updates = getHistory(startDate, endDate, otype, oid);
            System.out.println(updates.size());
            TreeMap<Date, ArrayList<Update>> updatesMap = groupByDate(updates);
            orderDateTime(updatesMap);
            request.setAttribute("updates", updatesMap);
        }
        catch (ParseException e) {
            // Alert the user to malformed date field
            request.setAttribute("exception", e);
        }
        catch (SQLException e) {
            // Alert the user to the streams
            request.setAttribute("exception", e);
        }

        request.getRequestDispatcher("/updates.jsp").forward(request, response);
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
    public List<Update> getHistory(Date start, Date end, String otype, String oid) throws SQLException
    {

        String query = "SELECT * FROM updates WHERE 1=1";
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

        logger.info(query);
        logger.info(params);
        return runner.query(query, handler, params.toArray());
    }

    /**
     * Sorts a list of updates into a TreeMap where updates are mapped to their day of occurrence.
     * @param updates List of updates spanning multiple days.
     * @return a mapping of updates by their day of occurrence.
     */
    private TreeMap<Date, ArrayList<Update>> groupByDate(List<Update> updates)
    {
        // Reverse the default ordering of TreeMap so that the most recent day appears at the top of the web page instead of the bottom.
        TreeMap<Date, ArrayList<Update>> dateList = new TreeMap<Date, ArrayList<Update>>(Collections.reverseOrder());

        // Map each update to its associated date.
        Date date = null;
        for(Update update: updates){
            date = update.getTime();
            if (!dateList.containsKey(date)){
                dateList.put(date, new ArrayList<Update>());
            }
            dateList.get(date).add(update);
        }
        return dateList;
    }

    /**
     * Orders daily updates by time of occurrence.
     * Reverses the default order of the TreeMap values so that more recent updates appear at the top instead of the bottom.
     * @param unOrdered
     */
    private void orderDateTime(TreeMap<Date, ArrayList<Update>> unOrdered)
    {
        for(Map.Entry<Date, ArrayList<Update>> entry: unOrdered.entrySet()){
            Collections.sort(entry.getValue());
            Collections.reverse(entry.getValue());
        }
    }
}
