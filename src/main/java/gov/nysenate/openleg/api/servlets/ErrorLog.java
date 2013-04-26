package gov.nysenate.openleg.api.servlets;
import gov.nysenate.openleg.model.Error;
import gov.nysenate.openleg.model.ViewReport;




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
	
	 private static final String VIEW_PATH = "/views/error-report.jsp";

	    
    /**
     * @see HttpServlet#HttpServlet()
     */
   

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ViewReport vr=new ViewReport();
		ArrayList<Error> error=vr.displayReports();
		request.setAttribute("errorList",error);
		getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
