package gov.nysenate.openleg;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SearchResultSet;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.util.SessionYear;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SearchServlet extends HttpServlet implements OpenLegConstants
{	
	
	private static long DATE_START = SessionYear.getSessionStart();
	private static long DATE_END = SessionYear.getSessionEnd();

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(SearchServlet.class);

	private SearchEngine2 searchEngine = null;
	
	public SearchServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
		
		try {
			searchEngine.closeSearcher();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.getParameter("reset")!=null)
			searchEngine.closeSearcher();
		
		String search = request.getParameter("search");
						
		String term = request.getParameter("term");
		String type = request.getParameter("type");
		
		String full = request.getParameter("full");
		String memo = request.getParameter("memo");
		String status = request.getParameter("status");
		String sponsor = request.getParameter("sponsor");
		String cosponsors = request.getParameter("cosponsors");
		String sameas = request.getParameter("sameas");
		String committee = request.getParameter("committee");
		String location = request.getParameter("location");
		
		String session = request.getParameter("session");
		
		if(search != null) {
			request.setAttribute("search", search);
			term = search;
		}
				
		String tempTerm = null;
		if((tempTerm = BillCleaner.getDesiredBillNumber(term)) != null) {
			term = "oid:" + tempTerm;
			type = "bill";
			
			System.out.println("!! " + tempTerm);
		}

		String sortField = request.getParameter("sort");
		boolean sortOrder = false;
		if (request.getParameter("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
		
		Date startDate = null; 
		Date endDate =  null;
		
		try {
			if (request.getParameter("startdate")!=null && (!request.getParameter("startdate").equals("mm/dd/yyyy")))
				startDate = OL_SEARCH_DATE_FORMAT.parse(request.getParameter("startdate"));
		} catch (java.text.ParseException e1) {
			logger.warn(e1);
		}
		
		try {
			if (request.getParameter("enddate")!=null && (!request.getParameter("enddate").equals("mm/dd/yyyy"))) {
				endDate = OL_SEARCH_DATE_FORMAT.parse(request.getParameter("enddate"));
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.set(Calendar.HOUR, 11);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				
				endDate = cal.getTime();
			}
		} catch (java.text.ParseException e1) {
			logger.warn(e1);
		}
		
		
		String format = "html";
		
		if (request.getParameter("format")!=null)
			format = request.getParameter("format");
		

		int pageIdx = 1;
		int pageSize = 20;
		
		if (request.getParameter("pageIdx") != null)
			pageIdx = Integer.parseInt(request.getParameter("pageIdx"));
		
		if (request.getParameter("pageSize") != null)
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		
		//now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		
		SearchResultSet srs;
		StringBuilder searchText = new StringBuilder();
		
		if (term != null)
			searchText.append(term);
		
		try {
			if (type != null && type.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("otype:");
				searchText.append(type.equals("res") ? "bill AND oid:r*":type);
			}
			
			if(session != null && session.length() > 0) {
				if(searchText.length() > 0)
					searchText.append(" AND ");
				
				searchText.append("year:" + session);
			}
			
			if (full != null && full.length() > 0)	{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("(full:\"");
				searchText.append(full);
				searchText.append("\"");
				
				searchText.append(" OR ");
				searchText.append("osearch:\"");
				searchText.append(full);
				searchText.append("\")");
			}
			
			if (memo != null && memo.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("memo:\"");
				searchText.append(memo);
				searchText.append("\"");
			}
			
			if (status != null && status.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("status:\"");
				searchText.append(status);
				searchText.append("\"");
			}
			
			if (sponsor != null && sponsor.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("sponsor:\"");
				searchText.append(sponsor);
				searchText.append("\"");
			}
			
			if (cosponsors != null && cosponsors.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("cosponsors:\"");
				searchText.append(cosponsors);
				searchText.append("\"");
			}
			
			if (sameas != null && sameas.length() > 0)
			{
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("sameas:");
				searchText.append(sameas);
			}

			if (committee != null && committee.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("committee:\"");
				searchText.append(committee);
				searchText.append("\"");
			}
			
			if (location != null && location.length() > 0) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("location:\"");
				searchText.append(location);
				searchText.append("\"");
			}
				
			if (startDate != null && endDate != null) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("when:[");
				searchText.append(startDate.getTime());
				searchText.append(" TO ");
				searchText.append(endDate.getTime());
				searchText.append("]");
			}
			else if (startDate != null) {
				if (searchText.length()>0)
					searchText.append(" AND ");
				
				searchText.append("when:[");
				searchText.append(startDate.getTime());
				searchText.append(" TO ");
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				
				startDate = cal.getTime();
				
				searchText.append(startDate.getTime());
				searchText.append("]");
			}
						
			term = searchText.toString();

			term = BillCleaner.billFormat(term);
						
			request.setAttribute("term", term);
			request.setAttribute("type", type);
						
			//default behavior is to return only active bills, so if a user searches
			//s1234 and s1234a is available then s1234a should be returned
			if(sortField == null && (((search != null && search.contains("otype:bill")) 
					|| (term != null && term.contains("otype:bill"))) 
					|| (type != null && type.equals("bill")))) {
				
				sortField = "sortindex";
				sortOrder = false;
				type = "bill";
			}
			
			if (sortField!=null && !sortField.equals("")) {
				request.setAttribute("sortField", sortField);
				request.setAttribute("sortOrder",Boolean.toString(sortOrder));
			}
			else {
				sortField = "when";
				sortOrder = true;
				request.setAttribute("sortField", sortField);
				request.setAttribute("sortOrder", Boolean.toString(sortOrder));
			}
			
			request.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
			request.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
			
			srs = null;
			
			if (term.length() == 0)	{
				response.sendError(404);
				return;
			}
			
			String searchFormat = "json";
												
			SenateResponse sr = null;
			if(term != null && !term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
				if(type != null && type.equals("bill")) {
					sr = searchEngine.search(term + " AND year:" + SessionYear.getSessionYear(),searchFormat,start,pageSize,sortField,sortOrder);
				}
				else {
					sr = searchEngine.search(term + " AND when:[" + DATE_START + " TO " + DATE_END + "]",searchFormat,start,pageSize,sortField,sortOrder);
					if(sr.getResults().isEmpty()) {
						sr = searchEngine.search(term + " AND year:" + SessionYear.getSessionYear(),searchFormat,start,pageSize,sortField,sortOrder);
					}
				}
			}
			else {
				System.out.println("!!! " + term);
				sr = searchEngine.search(term,searchFormat,start,pageSize,sortField,sortOrder);
			}
						
			srs = new SearchResultSet();
			srs.setTotalHitCount((Integer)sr.getMetadata().get("totalresults"));
			
			srs.setResults(ApiHelper.buildSearchResultList(sr));
			
			if (srs != null) {
				if (srs.getResults().size() == 0) {
					response.sendError(404);
				}
				else {
					request.setAttribute("results", srs);
					String viewPath = "/views/search-" + format + DOT_JSP;
					getServletContext().getRequestDispatcher(viewPath).forward(request, response);
				}
			}
			else {
				logger.error("Search Error: " + request.getRequestURI());
				response.sendError(500);
			}
			
		} catch (Exception e) {
			logger.error("Search Error: " + request.getRequestURI(),e);
			e.printStackTrace();
			response.sendError(500);
		}
	}

	public void init() throws ServletException {
		logger.info("SearchServlet:init()");
		
		searchEngine = SearchEngine2.getInstance();
		
	}
}
