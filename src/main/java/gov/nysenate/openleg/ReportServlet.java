package gov.nysenate.openleg;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.qa.ReportBuilder;
import gov.nysenate.openleg.qa.model.Report;
import gov.nysenate.openleg.qa.test.ReportedBill;
import gov.nysenate.openleg.qa.test.ReportedBillManager;
import gov.nysenate.openleg.qa.test.ReportedBillManager.BillType;

import java.io.IOException;
import java.util.Date;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

/**
 * Servlet implementation class ReportServlet
 */
public class ReportServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(ReportServlet.class);	
	
	private static final long serialVersionUID = 1L;
	
	private static final String VIEW_PATH = "/report/index.jsp";
	
	private static ReportedBillManager rbm = null;
       
    public ReportServlet() {
        super();
        rbm = new ReportedBillManager();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String format = request.getParameter("format");
		String uri = request.getRequestURI();
		
		TreeSet<ReportedBill> set = new TreeSet<ReportedBill>(new ReportedBill.ByHeat());
		set.addAll(rbm.rbr.findByBillType(BillType.PROBLEM_BILL));
		
		rbm.rbr.findByBillType(BillType.PROBLEM_BILL);
		
		if (uri.indexOf(".")!=-1)
			format = uri.substring(uri.indexOf(".")+1);
		
		if (format != null) {
			if (format.equals("json")) {
				JsonGenerator gen = ApiHelper.getMapper().getJsonFactory().createJsonGenerator(response.getWriter());
				gen.setPrettyPrinter(new DefaultPrettyPrinter());
				ApiHelper.getMapper().writeValue(gen, set);
			}
		}
		else {
			getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
