package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class ApiServlet2_0 extends HttpServlet implements OpenLegConstants {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ApiServlet2_0.class);	
	
	private static final String SRV_DELIM = "/";
	
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
		String arg1 = st.nextToken(); //2.0
		
		String command = "";		
		String term = "";
		
		if (st.hasMoreTokens())
		{
			command = st.nextToken();
			
			if (command.equals("2.0"))
				command = st.nextToken();
			
			if ((!command.startsWith("search")) && !st.hasMoreTokens())
			{
				term = command;
				command = arg1;
			}
		}
		else  {
			req.setAttribute("term","");
			getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
		}
				
		term = (String)req.getParameter("term");
		String key = term;
		int pageSize = req.getParameter("pageSize") != null ? 
							Integer.parseInt(req.getParameter("pageSize")) : 20;
		int pageIdx = (String)req.getParameter("pageIdx") != null ? 
							Integer.parseInt(req.getParameter("pageIdx")) : 1;
		Boolean sortOrder = Boolean.parseBoolean(req.getParameter("sortOrder"));
		String sortField = (String)req.getParameter("sort");
		String format = null;
		
		int start = (new Integer(pageIdx) -1) * (new Integer(pageSize));
		
		
		if(sortField == null) {
			sortField = "modified";
			sortOrder = true;
		}
		req.setAttribute("sortField",sortField);
		req.setAttribute("sortOrder",sortOrder);
		
		req.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
		req.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
		
		if(command.startsWith("search")) {
			if(term == null) {
				getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
				return;
			}
			
			String cmd[] = command.split("\\.");
			
			if(cmd.length == 1) {
				format = "html";
			}
			else {
				command = cmd[0];
				format = cmd[1];
			}
							
			req.setAttribute("format", format);
		}
		else {
			if (st.hasMoreTokens())
				term = st.nextToken();
			
			key = term;
			
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
				try {
					QueryBuilder queryBuilder = QueryBuilder.build().otype(command);
					
					if(term != null && !term.matches("\\s*")) queryBuilder.and().oid(term);
					
					term = queryBuilder.query();
				}
				catch(QueryBuilderException e) {
					logger.error(e);
				}
			}
			
			req.setAttribute("format", format);
			req.setAttribute(KEY_TYPE, command);
		}
		
		try {
			if (format.equalsIgnoreCase("html")) {
				if(command.equalsIgnoreCase("search")) {
					resp.sendRedirect("/legislation/search/?search=" + key);
				}
				else {
					resp.sendRedirect("/legislation/" + command + "/" + key);
				}
			}
			else {
				String sFormat = "json";
				String viewPath = "/views2/v2-api.jsp";
				
				if (format.equals("xml"))
					sFormat = "xml";
				
				SenateResponse sr = SearchEngine.getInstance().search(ApiHelper.dateReplace(term),sFormat,start,pageSize,sortField,sortOrder);			
				
				if(sr.getResults() == null || sr.getResults().size() == 0) {
					term = term+"*";
					sr = SearchEngine.getInstance().search(ApiHelper.dateReplace(term),sFormat,start,pageSize,sortField,sortOrder);
				}
				
				req.setAttribute("results", sr);
				
				if(sr == null || sr.getResults().size() == 0) {
					getServletContext().getRequestDispatcher("/legislation").forward(req, resp);
				}			
				else if(sr.getResults().size() == 1) {
				
					Result result = sr.getResults().get(0);
					
					if(!command.equals("search") && !term.contains(result.getOid())) {
						viewPath = TextFormatter.append("/legislation/api/2.0/",command,"/",result.getOid(),".",format);
						resp.sendRedirect(viewPath);
					}
					else {
						getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
					}
				}
				else {
					if(!command.equals("search") && !term.contains(sr.getResults().get(0).getOid())) {
						viewPath = TextFormatter.append("/legislation/2.0/search.",format,"?term=",term);
						resp.sendRedirect(viewPath);
					}
					else {
						getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
					}
				}
			}
			
		} catch (ParseException e) {
			logger.error(e);
		}
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
			logger.error(e);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy'T'HH-mm");
		
		QueryBuilder queryBuilder  = QueryBuilder.build();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		try {
			queryBuilder.otype(command).and().range("when", sdf.format(date), sdf.format(cal.getTime()));
		} catch (QueryBuilderException e) {
			logger.error(e);
		}
		
		return queryBuilder.query();
	}
}
