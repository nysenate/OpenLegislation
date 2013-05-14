package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.model.Update;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

/**
 * Servlet implementation class UpdateServlet
 */
public class UpdateServlet extends HttpServlet {

    private final SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");

    public UpdateServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        List<Update> updates = null;
        String startDay = request.getParameter("startDay");     // Earliest day we want to see results for.
        String endDay = request.getParameter("endDay");         // Latest day to see results for, default is today.
        String otype = request.getParameter("otype");
        if (request.getParameter("bill") != null) {
            // Show all changes for this bill
            String billId = request.getParameter("bill");
            try {
                updates = getBillHistory(billId);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {

            if (startDay == null || endDay == null){
                // Create a default date range.
                Date date = new Date();
                int numDaysToDisplay = -7;
                startDay = dateFormat.format(DateUtils.addDays(date, numDaysToDisplay));
                endDay = dateFormat.format(date);
            }
            request.setAttribute("endDay", endDay);
            request.setAttribute("startDay", startDay);
            try {
                startDay += " 00:00:00";
                endDay += " 23:59:59";
                if (otype != null && !otype.equalsIgnoreCase("all")) {
                    updates = processQueryByType(startDay, endDay, otype);
                } else {
                    updates = processQuery(startDay, endDay);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Create a map of updates.
        TreeMap<Date, ArrayList<Update>> updatesMap = groupByDate(updates);
        orderDateTime(updatesMap);
        request.setAttribute("updates", updatesMap);
        request.getRequestDispatcher("/updates.jsp").forward(request, response);
    }

    private List<Update> processQuery(String startDate, String endDate) throws SQLException
    {
        QueryRunner run = new QueryRunner(Application.getDB().getDataSource());
        ResultSetHandler<List<Update>> handler = new BeanListHandler<Update>(Update.class);
        List<Update> updates = run.query("SELECT * FROM updates WHERE date BETWEEN ? AND ? ORDER BY date", handler, startDate, endDate);
        return updates;
    }

    private List<Update> processQueryByType(String startDate, String endDate, String otype) throws SQLException
    {
        QueryRunner run = new QueryRunner(Application.getDB().getDataSource());
        ResultSetHandler<List<Update>> handler = new BeanListHandler<Update>(Update.class);
        List<Update> updates = run.query("SELECT * FROM updates WHERE otype = ? AND date BETWEEN ? AND ? ORDER BY date", handler, otype, startDate, endDate);
        return updates;
    }

    private List<Update> getBillHistory(String billId) throws SQLException
    {
        QueryRunner run = new QueryRunner(Application.getDB().getDataSource());
        ResultSetHandler<List<Update>> handler = new BeanListHandler<Update>(Update.class);
        List<Update> updates = run.query("SELECT * FROM updates WHERE oid = ?", handler, billId);
        return updates;
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
            try {
                date = dateFormat.parse(update.getDate());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
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
