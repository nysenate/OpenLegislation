package gov.nysenate.openleg.api.servlets;
import gov.nysenate.openleg.model.ViewReport;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ErrorLog
 */
public class ErrorLog extends HttpServlet
{
    private static final long serialVersionUID = 1L;
	private static final String VIEW_PATH = "/views/error-report.jsp";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
	    String val="request";
		String value=request.getParameter("val");

	    if(value!=null) {
	        val=value;
	    }

	    if(val.equals("report")) {
	        ViewReport vr=new ViewReport();
	        String id=request.getParameter("id");
	        int reportId=Integer.parseInt(id);
	        request.setAttribute("errorList",vr.displayReports(reportId));
	        getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
	    }
	    else {
    	    ViewReport vr=new ViewReport();
            request.setAttribute("reportList",vr.displayReportOption());
            getServletContext().getRequestDispatcher("/views/report.jsp").forward(request, response);
	    }

	}
}
