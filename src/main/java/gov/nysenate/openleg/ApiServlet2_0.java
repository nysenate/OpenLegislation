package gov.nysenate.openleg;

import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResultSet;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.xstream.XStreamBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		System.out.println(new SearchEngine2().search("otype:calendar AND when:[1265000400000 TO 1265043540000]","xml",0,5,null,true).getResults().size());
	
//		System.out.println(new ApiServlet2_0().dateReplace("blah blah04-01-2010T00-00blahblah"));
		
//		System.out.println(BillCleaner.formatV2Bill("s-5000"));
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
		
		st.nextToken(); //legislation
		st.nextToken(); //2.0
		
		String command = "";		
		if (st.hasMoreTokens())
			command = st.nextToken();
		else  {
			req.setAttribute("term","");
			getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
			
		}
		
		Date date = null;
		
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
			
			if(term.matches("(\\d{1,2}[-/]?){2}(\\d{2,4})?")) {
				term = formatDate(term, command);
			}
			else {
				
				if(command.equals("bill")) {
					term = BillCleaner.formatV2Bill(term);
				}
				
				term = "otype:" + command + ((term != null && !term.equals("")) ? " AND oid:" + term: "");
				
			}
			
			
			req.setAttribute("format", format);
			req.setAttribute(KEY_TYPE, command);
		}
		
		try {
			
			System.out.println(term+"\n"+dateReplace(term));
			
			SenateResponse sr = new SearchEngine2().search(dateReplace(term),format,start,pageSize,null,true);
			
			
			
			if(sr.getResults().size() == 0) {
				term = term+"*";
				sr = new SearchEngine2().search(dateReplace(term),format,start,pageSize,null,true);
			}
			
			req.setAttribute("results", sr);
			
			if(sr.getResults().size() == 0) {
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
		
		
		
	}
	
	public String dateReplace(String term) throws ParseException {
		Pattern  p = Pattern.compile("(\\d{1,2}[-]?){2}(\\d{2,4})T\\d{2}-\\d{2}");
		Matcher m = p.matcher(term);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy'T'KK-mm");
		
		while(m.find()) {
			String d = term.substring(m.start(),m.end());
			
			Date date = null;
			try {
				date = sdf.parse(d);
				term = term.substring(0, m.start()) + date.getTime() + term.substring(m.end());
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			
			m.reset(term);
			
		}
		
		return term;
	}
	
	public String formatDate(String term, String command) {
		Date date = null;
		
		if(term.matches("(\\d{1,2}[-/]?){2}(\\d{2,4})?")) {
			term = term.replace("/","-");
			
			Calendar c = Calendar.getInstance();
			if(term.matches("\\d{1,2}-\\d{1,2}"))
				term = term + "-" + c.get(Calendar.YEAR);
			if(term.matches("\\d{1,2}-\\d{1,2}-\\d{2}")) {
				
				String yr = term.split("-")[2];
				
				term = term.replaceFirst("-\\d{2}$","");
				
				term = term + "-" + Integer.toString(c.get(Calendar.YEAR)).substring(0,2) + yr;
			}
		}
		
		try {
			date = new SimpleDateFormat("MM-dd-yyyy").parse(term);
		}
		catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy'T'HH-mm");
		
		term = "when:[" + sdf.format(date) + " TO ";

		date.setHours(23);
		date.setMinutes(59);
		date.setSeconds(59);
		
		term = term + sdf.format(date) + "]";
		
		return "otype:" + command + " AND " + term;
		
	}
}
