package gov.nysenate.openleg;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.BillCleaner;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SearchServlet extends HttpServlet implements OpenLegConstants {	

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(SearchServlet.class);
	
	public SearchServlet() {
		super();
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
		
		SenateResponse sr = null;
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		if (term != null)
			queryBuilder.insertBefore(term);
		
		try {
			if (valid(type)) {
				queryBuilder.and().otype(type);
				if(type.equals("res")) queryBuilder.and().oid("r*");
			}
			
			if(valid(session))
				queryBuilder.and().keyValue("year", session);
			
			if (valid(full))
				queryBuilder.and()
					.append("(")
					.keyValue("full", full, "\"")
					.or()
					.keyValue("osearch", full, "\"")
					.append(")");
			
			if (valid(memo))
				queryBuilder.and().keyValue("memo", memo, "\"");
			
			if (valid(status))
				queryBuilder.and().keyValue("status", status, "\"");
			
			if (valid(sponsor))
				queryBuilder.and().keyValue("sponsor", sponsor, "\"");
			
			if (valid(cosponsors))
				queryBuilder.and().keyValue("cosponsors", cosponsors, "\"");
			
			if (valid(sameas))
				queryBuilder.and().keyValue("sameas", sameas);

			if (valid(committee))
				queryBuilder.and().keyValue("committee", committee, "\"");
			
			if (valid(location))
				queryBuilder.and().keyValue("location", location, "\"");
				
			if (startDate != null && endDate != null) {
				queryBuilder.and().range("when", 
						Long.toString(startDate.getTime()), 
						Long.toString(endDate.getTime()));
			}
			else if (startDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				
				queryBuilder.and().range("when", 
						Long.toString(startDate.getTime()), 
						Long.toString(cal.getTimeInMillis()));
			}
						
			term = queryBuilder.query();
			
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
						
			if (term.length() == 0)	{
				response.sendError(404);
				return;
			}
			
			String searchFormat = "json";
			
			if(term != null && !term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
				sr = SearchEngine.getInstance().search(queryBuilder.and().current().query(),
						searchFormat,start,pageSize,sortField,sortOrder);
			}
			else {
				sr = SearchEngine.getInstance().search(term,searchFormat,start,pageSize,sortField,sortOrder);
			}
					
			ApiHelper.buildSearchResultList(sr);
			
			if (sr != null) {
				if (sr.getResults().size() == 0) {
					response.sendError(404);
				}
				else {
					request.setAttribute("results", sr);
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
	
	public boolean valid(String str) {
		return str != null && str.length() > 0;
	}

	public void init() throws ServletException {
		logger.info("SearchServlet:init()");
	}
}
