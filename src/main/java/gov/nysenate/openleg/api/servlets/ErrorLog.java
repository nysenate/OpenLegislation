package gov.nysenate.openleg.api.servlets;
import gov.nysenate.openleg.model.Error;
import gov.nysenate.openleg.model.Report;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class ErrorLog
 */
public class ErrorLog extends HttpServlet
{
    private static Logger logger = Logger.getLogger(ErrorLog.class);

    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	    String jspPath;
	    String reportIdParam = request.getParameter("id");
	    if (reportIdParam == null) {
	        List<Report> reportList;
	        try {
	            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
	            reportList = runner.query("SELECT * FROM report", new BeanListHandler<Report>(Report.class));
	        }
	        catch (SQLException e) {
	            logger.error(e);
	            reportList =  new ArrayList<Report>();
	        }
            request.setAttribute("reportList",displayReportOption());
            jspPath = "/views/report.jsp";
	    }
	    else {
            int reportId=Integer.parseInt(reportIdParam);
            request.setAttribute("errorList",displayReports(reportId));
            jspPath = "/views/error-report.jsp";
	    }

        getServletContext().getRequestDispatcher(jspPath).forward(request, response);
	}

    public List<Error> displayReports(int id)
    {
        try {
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            return runner.query("SELECT * FROM error WHERE reportId = ? ORDER BY billId", new BeanListHandler<Error>(Error.class), id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Error>();
        }
    }

    public List<Report> displayReportOption()
    {
        try {
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            return runner.query("SELECT * FROM report", new BeanListHandler<Report>(Report.class));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Report>();
        }
    }
}
