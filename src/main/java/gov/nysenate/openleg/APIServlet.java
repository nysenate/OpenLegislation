package gov.nysenate.openleg;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResultSet;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.SessionYear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class APIServlet extends HttpServlet implements OpenLegConstants {

	private static long DATE_START = SessionYear.getSessionStart();
	private static long DATE_END = SessionYear.getSessionEnd();
	
	private static final long serialVersionUID = -7567155903739799800L;

	private static Logger logger = Logger.getLogger(APIServlet.class);	

	private static final String SRV_DELIM = "/";
	
	private static SearchEngine2 searchEngine = null;
	
	@Override
	public void init() throws ServletException {
		super.init();
	
		if (searchEngine == null)
			searchEngine = SearchEngine2.getInstance();
		
	}

	public static SearchEngine2 getSearchEngineInstance ()	{
		return searchEngine;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		if (searchEngine == null)
			searchEngine = SearchEngine2.getInstance();
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		if (req.getParameter("reset")!=null) {
			if (searchEngine != null)
				searchEngine.closeSearcher();
			
			searchEngine = SearchEngine2.getInstance();
			
			return;
		}
		
		/* /legislation/<service> */
		String service;
		
		/* /legislation/<service>/<version> */
		String version = "";
		
		/* accepted formats:
		 * 		bill:		html, json, mobile, xml		csv, html-print, lrs-print
		 * 		calendar:	html, json, mobile, xml
		 * 		meeting:	html, json, mobile, xml
		 * 		search:		html, json, mobile, xml		html-list, rss
		 * 		transcript:	html, json, mobile, xml		csv
		 */
		String format = null;
		
		/* accepted types: bill, calendar, meeting, transcript
		 * 		as well as bills, calendars, meetings, transcripts, actions, votes
		 */
		String type = null;
		
		/* unique id */
		String key = "";
		
		String uri = java.net.URLDecoder.decode(req.getRequestURI(),OpenLegConstants.ENCODING);
		req.setAttribute(KEY_PATH,uri);
			
		logger.info("request: " + uri + " (" + req.getRequestURI() + ")");
		
		
		StringTokenizer st = new StringTokenizer (uri,SRV_DELIM);
		st.nextToken(); //legislation
		service = st.nextToken();
		
		if (st.hasMoreTokens())
			version = st.nextToken(); //1.0

		int pageIdx = DEFAULT_START_PAGE;
		int pageSize = DEFAULT_PAGE_SIZE;
				
		try
		{	
			/*
			 * /legislation/api/1.0/
			 * 					<format>/
			 * 					<type>/
			 * 					<id>/
			 * 	
			 * ex. /legislation/api/1.0/html/bill/s1234-2011
			 */
			if (version.equals("1.0")) {				
				format = st.nextToken().toLowerCase();	
				type = st.nextToken().toLowerCase();
				
				if (st.hasMoreTokens())
					key = st.nextToken();
				
				if (st.hasMoreTokens()) {
					pageIdx = Integer.parseInt(st.nextToken());
						
					if (st.hasMoreTokens())
						pageSize = Integer.parseInt(st.nextToken());
							
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
				
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
			/*
			 * /legislation/api/
			 * 				<format>/
			 * 				<type>/
			 * 				<id>/
			 * 
			 * ex. /legislation/api/html/bill/s1234-2011
			 */
			else if (service.equals("api")) {
				format = version;
				type = st.nextToken();
				key = st.nextToken();
								
				if (st.hasMoreTokens()) {
					pageIdx = Integer.parseInt(st.nextToken());
						
					if (st.hasMoreTokens())
						pageSize = Integer.parseInt(st.nextToken());
							
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
					
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
			/*
			 * /legislation/
			 * 			<type>/
			 * 			<id>/
			 * 
			 * ex. /legislation/bill/s1234-2011
			 * 
			 * /legislation/
			 * 			<type>/
			 * 			?<id>
			 * 
			 * ex. /legislation/bill/?s1234-2011
			 */
			else 
			{
				format = "html";
				
				if (req.getSession().getAttribute("mobile")!=null)
					format = "mobile";
				
				type = service;
				key = version;
				
				if (key.length()==0 && req.getParameterNames().hasMoreElements())
					key = req.getParameterNames().nextElement().toString();
				
				if (st.hasMoreTokens()) {
					pageIdx = Integer.parseInt(st.nextToken());
						
					if (st.hasMoreTokens())
						pageSize = Integer.parseInt(st.nextToken());
							
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
				
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
		}
		catch (NumberFormatException nfe) {
			logger.warn ("Invalid API call", nfe);
		}
		catch (NoSuchElementException nse) {
			logger.warn ("Invalid API call", nse);
		}
		catch (NullPointerException npe) {
			logger.warn ("Invalid API call", npe);
		}
		catch (Exception e)	{
			logger.warn ("Invalid API call", e);
		}
		
	}
	
	public void handleAPIv1 (String format, String type, String key, int pageIdx, int pageSize, HttpServletRequest req, HttpServletResponse resp) 
		throws IOException, ServletException {

		String viewPath = "";
		String searchString = "";
		String originalType = type;
		String sFormat = "json";
		String sortField = "when";
		boolean sortOrder = true;

		if(type != null) {
			if(type.contains("bill")) {
				sortField = "sortindex";
				sortOrder = false;
			}
			else if(type.contains("meeting")) {
				sortField = "sortindex";
			}
		}
		
		
		SenateResponse sr = null;
		
		
		if (type.equalsIgnoreCase("sponsor")) {
			String filter = req.getParameter("filter");
			key = "sponsor:\"" + key + (filter != null ? " AND " + filter : "");
			type = "bills";
		}
		else if (type.equalsIgnoreCase("committee")) {
			key = "committee:\"" + key + "\" AND oid:s*-" + SessionYear.getSessionYear();
			type = "bills";
		}
		
		key = key.trim();
		
		if (pageSize > MAX_PAGE_SIZE)
			throw new ServletException ("The maximum page size is " + MAX_PAGE_SIZE);
		//now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;
		
		
		logger.info("request: key=" + key + ";type=" + type + ";format=" + format + ";paging=" + start + "/" + end);
		try	{
			/*
			 * construct query with "otype:<type>" if type present
			 */
			if (type != null) {
				
				/*
				 * for searches
				 * 
				 * applicable to: bills, calendars, meetings, transcripts, actions, votes
				 */
				if (type.endsWith("s"))	{
					type = type.substring(0,type.length()-1);
					
					searchString = "otype:" + type;
					
					if (key != null && key.length() > 0)
						searchString += " AND " + key;
				}
				/*
				 * for individual documents
				 * 
				 * applicable to: bill, calendar, meeting, transcript
				 */
				else {
					searchString ="otype:" + type;
	
					if (key != null && key.length() > 0) {
						if (type.equals("bill") && key.indexOf("-") == -1)
							key += "-" + DEFAULT_SESSION_YEAR;
	
						key = key.replace(" ","+");
						searchString += " AND oid:" + key;
					}
				}
			}
			else
			{
				searchString = key;
			}
			
			req.setAttribute("sortField", sortField);
			req.setAttribute("sortOrder", Boolean.toString(sortOrder));
			req.setAttribute("type", type);
			req.setAttribute("term", searchString);
			req.setAttribute("format", format);
			req.setAttribute("key", key);
			req.setAttribute(PAGE_IDX,pageIdx+"");
			req.setAttribute(PAGE_SIZE,pageSize+"");
						
			/* 
			 * applicable to: bills
			 * 
			 * only return bills with "year:<currentSessionYear>"
			 */
			if(originalType.equals("bills")) {
				sr = searchEngine.search(ApiHelper.dateReplace(searchString) 
						+ " AND year:" + SessionYear.getSessionYear() 
						+ " AND active:true",
						sFormat,start,pageSize,sortField,sortOrder);
			}
			/*
			 * applicable to: calendars, meetings, transcripts
			 * 
			 * only want current session documents as defined by the time
			 * frame between DATE_START and DATE_END
			 */
			else if(originalType.endsWith("s")) {
				sr = searchEngine.search(ApiHelper.dateReplace(searchString) 
						+ " AND when:[" 
						+ DATE_START 
						+ " TO " 
						+ DATE_END + "]"
						+ " AND active:true",
						sFormat,start,pageSize,sortField,sortOrder);
			}
			/*
			 * applicable to: individual documents
			 * 
			 * attempting to access a document by id doesn't
			 * doesn't require any special formatting
			 */
			else {
				sr = searchEngine.search(ApiHelper.dateReplace(searchString),sFormat,start,pageSize,sortField,sortOrder);
			}
			
			logger.info("got search results: " + sr.getResults().size());
						
			if (sr.getResults().size()==0) {
				resp.sendError(404);
				return;
			}
			else if (sr.getResults().size()==1 && format.matches("(html(\\-print)?|mobile|lrs\\-print)"))	{
				
				Result result = sr.getResults().get(0);
				
				/* if not an exact match on the oid it's likely 
				 * the correct id, just the wrong case */
				if (!result.getOid().equals(key)) {
					if(format.equals("html"))
						resp.sendRedirect("/legislation/" 
								+ result.getOtype() 
								+ "/" 
								+ result.getOid());
					else
						resp.sendRedirect("/legislation/api/1.0/" 
								+ format 
								+ "/" 
								+ result.getOtype() 
								+ "/" 
								+ result.getOid());
					return;
				}
				
				String jsonData = result.getData();
				req.setAttribute("active", result.getActive());
								
				jsonData = jsonData.substring(jsonData.indexOf(":")+1);
				jsonData = jsonData.substring(0,jsonData.lastIndexOf("}"));
				
				/* Jackson ObjectMaper will deserialize our json in to an object,
				 * we need to know what sort of an object that is
				 */
				String className = "gov.nysenate.openleg.model.bill." + type.substring(0,1).toUpperCase() + type.substring(1);
				
				/* 
				 * for bills we populate other relevant data, such as
				 * 		votes, calendars, meetings, sameas bills, older versions, etc.
				 */
				if (type.equals("bill")) {
					String billQueryId = key;
					String sessionYear = DEFAULT_SESSION_YEAR;
					
					/* default behavior to maintain previous permalinks
					 * is if key=S1234 to transform to S1234-2009
					 * in line with our new bill oid format <billno>-<sessYear> */
					String[] billParts = billQueryId.split("-");
					billQueryId = billParts[0];
					if (billParts.length > 1)
						sessionYear = billParts[1];
				
					/* turns S1234A in to S1234 */
					String billWildcard = billQueryId;
					if (!Character.isDigit(billWildcard.charAt(billWildcard.length()-1)))
						billWildcard = billWildcard.substring(0,billWildcard.length()-1);
						
					//get BillEvents for this 
					//otype:action AND billno:((S1234-2011 OR [S1234A-2011 TO S1234Z-2011) AND S1234*-2011)
					String rType = "action";
					String rQuery = null;
					rQuery = billWildcard + "-" + sessionYear;
					
					logger.info(rQuery);
					
					rQuery = "billno:((" 
						+ rQuery 
	                        + " OR [" + billWildcard + "A-" + sessionYear 
	                           + " TO " + billWildcard + "Z-" + sessionYear
	                        + "]) AND " + billWildcard + "*-" + sessionYear + ")";

					ArrayList<SearchResult> relatedActions = ApiHelper.getRelatedSenateObjects(rType,rQuery);
					Hashtable<String,SearchResult> uniqResults = new Hashtable<String,SearchResult>();
					for (SearchResult rResult: relatedActions) {
						BillEvent rAction = (BillEvent)rResult.getObject();
						uniqResults.put(rAction.getEventDate().toGMTString()+'-'+rResult.getTitle().toUpperCase(), rResult);
						
					}
					
					ArrayList<SearchResult> list = Collections.list(uniqResults.elements());
					Collections.sort(list);
					req.setAttribute("related-" + rType, list);

					//get older bills (e.g. for S1234A get S1234)
					//otype:meeting AND oid:((S1234-2011 OR [S1234A-2011 TO S1234Z-2011) AND S1234*-2011)
					rType = "bill";
					rQuery = billWildcard + "-" + sessionYear;
					
					logger.info(rQuery);
					
					rQuery = "oid:((" 
						+ rQuery 
	                        + " OR [" + billWildcard + "A-" + sessionYear 
	                           + " TO " + billWildcard + "Z-" + sessionYear
	                        + "]) AND " + billWildcard + "*-" + sessionYear + ")";
					
					req.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects (rType,rQuery));

					//get Meetings
					//otype:meeting AND bills:"S67005-2011"
					rType = "meeting";
					rQuery = "bills:\"" + key + "\"";					
					req.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
					
					//get calendars
					//otype:calendar AND  bills:"S337A-2011"
					rType = "calendar";
					 rQuery = "bills:\"" + key + "\"";
					req.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
					
					//get votes
					//otype:vote AND billno:"A11597-2011"
					rType = "vote";
					 rQuery = "billno:\"" + key + "\"";
					 					 
					req.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
				}
				else if (type.equals("calendar")) {
					className = "gov.nysenate.openleg.model.calendar.Calendar";
				}
				else if (type.equals("meeting")) {
					className = "gov.nysenate.openleg.model.committee.Meeting";
				}
				else if(type.equals("transcript")) {
					className = "gov.nysenate.openleg.model.transcript.Transcript";
				}
				
				Object resultObj = null;
				
				try	{
					resultObj = ApiHelper.getMapper().readValue(jsonData,  Class.forName(className));
				}
				catch (Exception e) {
					logger.warn("error binding className", e);
				}
				
				req.setAttribute(type, resultObj);
				viewPath = "/views/" + type + "-" + format + ".jsp";
			}
			else {
				/* all non html/print bill views go here */
				if (type.equals("bill") && (!format.equals("html"))) {
					viewPath = "/views/bills-" + format + ".jsp";
					
					ArrayList<SearchResult> searchResults = ApiHelper.buildSearchResultList(sr);
					ArrayList<Bill> bills = new ArrayList<Bill>();
					
					for (SearchResult result : searchResults) {
						bills.add((Bill)result.getObject());
					}
										
					req.setAttribute("bills", bills);
				}
				/* all calendar, meeting, transcript multi views go here */
				else
				{
					viewPath = "/views/" + "search" + "-" + format + ".jsp";
					
					SearchResultSet srs = new SearchResultSet();
					srs.setTotalHitCount((Integer)sr.getMetadata().get("totalresults"));
					srs.setResults(ApiHelper.buildSearchResultList(sr));
					
					req.setAttribute("results", srs);
				}
			}
		}
		catch (Exception e) {
			logger.warn("search controller didn't work for: " + req.getRequestURI(),e);
			e.printStackTrace();
		}

		try {
			logger.info("routing to search controller:" + viewPath);
			getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
		}
		catch (Exception e)	{
			logger.warn("search controller didn't work for: " + req.getRequestURI(),e);
		}
	}
}
