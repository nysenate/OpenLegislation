package gov.nysenate.openleg.api.servlets.admin;

import gov.nysenate.openleg.model.Report;
import gov.nysenate.openleg.model.ReportError;
import gov.nysenate.openleg.model.ReportObservation;
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
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class ErrorLog
 */
@SuppressWarnings("serial")
public class ReportsServlet extends HttpServlet
{
    private static Logger logger = Logger.getLogger(ReportsServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	    String jspPath;
	    String reportIdParam = request.getParameter("id");
	    QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
	    if (reportIdParam == null) {
	        List<Report> reportList;
	        try {
	            reportList = runner.query("SELECT * FROM report", new BeanListHandler<Report>(Report.class));
	            for (Report report : reportList) {
	                report = getReport(runner, report);
	                logger.info("REPORT "+report);
	                for (ReportError error : report.getNewErrors()) {
	                    logger.info("["+error.getId()+"] "+error.getOid()+" - "+error.getField());
	                }
	                logger.info("");
	                logger.info("");
	            }
	        }
	        catch (SQLException e) {
	            logger.error(e);
	            reportList =  new ArrayList<Report>();
	        }
            request.setAttribute("reportList",reportList);
            jspPath = "/admin/reports.jsp";
	    }
	    else {
            Report report = null;
            try {
                report = getReport(runner, Integer.parseInt(reportIdParam));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            request.setAttribute("report",report);
            jspPath = "/admin/reports-view.jsp";
	    }

        getServletContext().getRequestDispatcher(jspPath).forward(request, response);
	}

	public Report getReport(QueryRunner runner, int reportId) throws SQLException {
        Report report = runner.query("SELECT * from report WHERE id = ?", new BeanHandler<Report>(Report.class), reportId);
        return getReport(runner, report);
	}

	public Report getReport(QueryRunner runner, Report report) throws SQLException {
        report.setNewErrors(runner.query("SELECT * FROM report_error WHERE openedAt > DATE_SUB(?, INTERVAL 7 DAY) AND openedAt <= ?", new BeanListHandler<ReportError>(ReportError.class),report.getTime(), report.getTime()));
        report.setClosedErrors(runner.query("SELECT * FROM report_error WHERE closedAt > DATE_SUB(?, INTERVAL 7 DAY) AND closedAt <= ?", new BeanListHandler<ReportError>(ReportError.class),report.getTime(), report.getTime()));
        report.setOpenErrors(runner.query("SELECT * FROM report_error WHERE openedAt < ? AND (closedAt > ? OR closedAt IS NULL)", new BeanListHandler<ReportError>(ReportError.class), report.getTime(), report.getTime()));
        report.setObservations(runner.query("SELECT * FROM report_observation WHERE reportId = ? ORDER BY oid", new BeanListHandler<ReportObservation>(ReportObservation.class), report.getId()));
        return report;
	}
}
