package gov.nysenate.openleg;

import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResultSet;
import gov.nysenate.openleg.util.BillCleaner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class ApiServlet2_0 extends HttpServlet implements OpenLegConstants {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(APIServlet.class);	
	
	private static final String SRV_DELIM = "/";
	
	public static void main(String[] args) {
		System.out.println(new SearchEngine2().get("xml","bill","s1234",null,0,1,true).getResults().iterator().next());
	}
       
    public ApiServlet2_0() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {

    	doPost(request, response);
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String encodedUri = req.getRequestURI();
		
		String uri = java.net.URLDecoder.decode(encodedUri,OpenLegConstants.ENCODING);
			
		logger.info("request: " + uri + " (" + encodedUri + ")");
		
		req.setAttribute(KEY_PATH,uri);
	
		StringTokenizer st = new StringTokenizer (uri,SRV_DELIM);
		
		String app = st.nextToken(); //legislation
		String version = st.nextToken(); //2.0
		
		String command = "";		
		if (st.hasMoreTokens())
			command = st.nextToken();
		else  {
			req.setAttribute("term","");
			getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
			
		}
		
		String term = (String)req.getParameter("term");
		int pageSize = req.getParameter("pageSize") != null ? 
							Integer.parseInt(req.getParameter("pageSize")) : 20;
		int pageIdx = (String)req.getParameter("pageIdx") != null ? 
							Integer.parseInt(req.getParameter("pageIdx")) : 1;
		Boolean sortOrder = Boolean.parseBoolean(req.getParameter("sortOrder"));
		String sortField = (String)req.getParameter("sort");
		String session = (String)req.getParameter("session");
		
		int start = (new Integer(pageIdx) -1) * (new Integer(pageSize));
		
		if(sortField != null) {
			req.setAttribute("sortField",sortField);
			req.setAttribute("sortOrder",sortOrder);
			
		}
		
		req.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
		req.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
		
		try {
			if(command.equals("search")) {
				if(term == null) {
					getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
				
				}
				String format = st.nextToken();
				
				req.setAttribute("term",term);
				
				SearchResultSet srs = new SearchEngine2().search(term, start, pageSize, sortField, sortOrder);
				
				if (srs != null)
				{
					req.setAttribute("results", srs);
					String viewPath = "/views/search-" + format + DOT_JSP;
					getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
					
				}
				else
				{					
					logger.error("Search Error: " + req.getRequestURI());
					resp.sendError(500);
					
				}
				
			}
			else {
				String key = st.nextToken();
				
				String format = "";
				try {
					format = uri.split("/")[uri.split("/").length-1].split("\\.")[1];
					key = key.split("\\.")[0];
				}
				catch (Exception e) {
					format = "html";
				}
				
				req.setAttribute("format", format);
				req.setAttribute(KEY_TYPE, command);
				req.setAttribute("term", key);
				
				String viewPath = (format.equals("html")? "/views/" + command + "-" + format + ".jsp" : "/views2/v2-api.jsp");
								
				getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
				
			}
			/*else if(command.equals("bill")) {
				
			}
			else if(command.equals("transcript")) {
				
			}
			else if(command.equals("meeting")) {
		
			}
			else if(command.equals("calendar")) {
				
			}
			else if(command.equals("person")) {
				
			}*/
		}
		catch (ParseException e) {
			logger.error("Search Error: " + req.getRequestURI(),e);
		
			resp.sendError(500);
		
		}
	}
}
