package gov.nysenate.openleg;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.qa.Report;
import gov.nysenate.openleg.qa.ReportBuilder;

import java.io.IOException;
import java.util.Date;

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
	
	private static Report report = null;
	private static long timeGenerated = 0L;
	
	private static final String VIEW_PATH = "/report/index.html";
       
    public ReportServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String format = request.getParameter("format");
		String uri = request.getRequestURI();
		
		if (uri.indexOf(".")!=-1)
			format = uri.substring(uri.indexOf(".")+1);
		
		if (format != null) {
			if (format.equals("json")) {
				JsonGenerator gen = ApiHelper.getMapper().getJsonFactory().createJsonGenerator(response.getWriter());
				gen.setPrettyPrinter(new DefaultPrettyPrinter());
				ApiHelper.getMapper().writeValue(gen, getReport());
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
	
	public static Report getReport() {
		if(report == null) {
			generateReport();
		}
		else {
			if(new Date().getTime() - timeGenerated > 900000) {
				return generateReport();
			}
		}
		return report;
	}
	
	private static Report generateReport() {
		logger.info("Generating a new report");
		
		timeGenerated = new Date().getTime();
		try {
			report = new ReportBuilder().run();
		} catch (ParseException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return report;
	}

}
