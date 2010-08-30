package gov.nysenate.openleg;

import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResultSet;
import gov.nysenate.openleg.search.SenateResponse;
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
	
	public static void main(String[] args) throws ParseException, IOException {
		System.out.println(new SearchEngine2().search("otype:supplemental","xml",0,1,null,true).getResults().iterator().next().data);
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
		String format = null;
		
		int start = (new Integer(pageIdx) -1) * (new Integer(pageSize));
		
		
		if(sortField != null) {
			req.setAttribute("sortField",sortField);
			req.setAttribute("sortOrder",sortOrder);
			
		}
		
		req.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
		req.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
		
		
		
		if(command.equals("search")) {
			if(term == null) {
				getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
			
			}
			format = st.nextToken();				
			req.setAttribute("format", format);
		}
		else {
			term = st.nextToken();
			
			format = "";
			try {
				format = uri.split("/")[uri.split("/").length-1].split("\\.")[1];
				term = term.split("\\.")[0];
			}
			catch (Exception e) {
				format = "html";
			}
			
			term = "otype:" + command + (term != null && (!term.equals(""))? " AND oid:" + term + "*" : "");
			
			req.setAttribute("format", format);
			req.setAttribute(KEY_TYPE, command);
			
			System.out.println(term + " : " + format);
		}
		
			try {
			SenateResponse sr = new SearchEngine2().search(term,format,start,pageSize,null,true);
			req.setAttribute("results", sr);
			
			if(sr.getResults().size() == 1) {
				getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
			}
			else if(sr.getResults().size() == 1) {
				String viewPath = "/views2/v2-api.jsp";
				if(!command.equals("search") && !term.contains(sr.getResults().iterator().next().getOid())) {
					viewPath = "/legislation/2.0/" + command + "/" + sr.getResults().iterator().next().getOid() + "." + format;
					resp.sendRedirect(viewPath);
				}
				else {
					getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
				}
			}
			else {
				String viewPath = "/views2/v2-api.jsp";
				if(!command.equals("search") && !term.contains(sr.getResults().iterator().next().getOid())) {
					viewPath = "/legislation/2.0/search/" +format +"?term=" + term;
					resp.sendRedirect(viewPath);
				}
				else {
					getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
				}
				
			}
		} catch (ParseException e) {
			e.printStackTrace();
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
}
