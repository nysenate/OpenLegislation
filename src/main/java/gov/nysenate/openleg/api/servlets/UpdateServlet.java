package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.model.Update;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/**
 * Servlet implementation class UpdateServlet
 */
public class UpdateServlet extends HttpServlet {

    public UpdateServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        List<Update> updates = null;
        // Strings in yyyy-MM-dd format inputed by user in the html date input.
        String startDay = (String)request.getParameter("startDay");
        String endDay = (String)request.getParameter("endDay");
        // Strings in yyyy-MM-dd HH:mm:ss format, used in MySQL queries.
        String startDate = null;
        String endDate = null;
        if(startDay == null || endDay == null){
            startDate = makeDefaultStartDate();
            endDate = makeDefaultEndDate();
            endDay = endDate.split(" ")[0];
            startDay = startDate.split(" ")[0];
        }
        else {
            startDate = formatStartDateString(startDay);
            endDate = formatEndDateString(endDay);
        }
        request.setAttribute("endDay", endDay);
        request.setAttribute("startDay", startDay);
        try {
            updates = processQuery(startDate, endDate);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        // Create a map of updates by date.
        TreeMap<Date, ArrayList<Update>> updatesMap = groupByDate(updates);
        orderDateTime(updatesMap);
        request.setAttribute("updates", updatesMap);
        request.getRequestDispatcher("/updates.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // TODO: Move most(all?) this code out of servlet.
    
    private List<Update> processQuery(String startDate, String endDate) throws SQLException
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

        QueryRunner run = new QueryRunner(datasource);
        ResultSetHandler<List<Update>> handler = new BeanListHandler<Update>(Update.class);
        List<Update> updates = run.query("SELECT * FROM OpenLegUpdateTracker.Updates WHERE date BETWEEN ? AND ? ORDER BY date", handler, startDate, endDate);
        return updates;
    }

    private String makeDefaultEndDate()
    {
        // Default end date = current date.
        Date date = new Date();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endDate = sdf.format(date);
        return endDate;
    }

    private String makeDefaultStartDate()
    {
        // Default range starts from 7 days ago.
        int defaultRange = -7;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Set to the beginning of the day.
        DateUtils.setMilliseconds(date, 0);
        DateUtils.setMinutes(date, 0);
        DateUtils.setHours(date, 0);
        String startDate = sdf.format(DateUtils.addDays(date, defaultRange));
        return startDate;
    }

    /**
     * Sorts updates by their day of occurrence.
     * @param updates List of updates to be group by day of occurrence.
     * @return a mapping of updates to each day.
     */
    private TreeMap<Date, ArrayList<Update>> groupByDate(List<Update> updates)
    {
        // Store the most recent days first.
        TreeMap<Date, ArrayList<Update>> dateList = new TreeMap<Date, ArrayList<Update>>(Collections.reverseOrder());
        for(Update update: updates){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(update.getDate());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
            if (!dateList.containsKey(date)){
                dateList.put(date, new ArrayList<Update>());
            }
            dateList.get(date).add(update);
        }
        return dateList;
    }


    /*
     * Orders all updates in a day by their time of occurrence. Starting with the most recent.
     */
    private void orderDateTime(TreeMap<Date, ArrayList<Update>> unOrdered)
    {
        for(Map.Entry<Date, ArrayList<Update>> entry: unOrdered.entrySet()){
            Collections.sort(entry.getValue());
            Collections.reverse(entry.getValue());
        }
    }

    /**
     * Converts a string representation of a date into a string format recognizable by MySQL.
     * Since its the starting date, we want HH:mm:ss to be zeros so the day specified is included
     *@param startDay string representing a date in yyyy-MM-dd format
     *@return String representing the same date in yyyy-MM-dd HH:mm:ss format.
     */
    private String formatStartDateString(String startDay)
    {
        String formatted = startDay + " 00:00:00";
        return formatted;
    }

    /**
     * Converts a string representation of a date into a string format recognizable by MySQL.
     * Since its the end date, we want HH:mm:ss to be 23:59:59, so the day specified is included.
     *@param startDay string representing a date in yyyy-MM-dd format
     *@return String representing the same date in yyyy-MM-dd HH:mm:ss format.
     */
    private String formatEndDateString(String endDay)
    {
        String formatted = endDay + " 23:59:59";
        return formatted;
    }

}
