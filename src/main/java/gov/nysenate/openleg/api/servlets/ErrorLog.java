package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.model.ReportCreate;
import gov.nysenate.openleg.model.Error;



import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ErrorLog
 */
public class ErrorLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private static  ReportCreate rr = null;
	 private static final String VIEW_PATH = "/views/error-report.jsp";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ErrorLog() {
        super();
        rr = new ReportCreate();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ReportCreate.checkFiles();
		rr.insertReport();
		ArrayList<Error> errorList=rr.createErrorReport();
		request.setAttribute("errorList",errorList);
		getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
