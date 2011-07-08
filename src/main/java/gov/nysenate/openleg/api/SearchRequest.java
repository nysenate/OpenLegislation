package gov.nysenate.openleg.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.TextFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SearchRequest extends AbstractApiRequest {
	private final Logger logger = Logger.getLogger(SearchRequest.class);
	
	String type;
	String term;
	
	public SearchRequest(HttpServletRequest request, HttpServletResponse response,
			String format, String type, String term, String pageNumber, String pageSize) {
		super(request, response, pageNumber, pageSize, format, getApiEnum(SearchView.values(),type));
		this.type = type;
		try {
			this.term = URLDecoder.decode(whichTerm(term),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
	}
	
	@Override
	public void fillRequest() throws ApiRequestException {
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
		
		String sortField = request.getParameter("sort");
		boolean sortOrder = false;
		if (request.getParameter("sortOrder")!=null)
			sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
		
		Date startDate = null; 
		Date endDate =  null;
		
		try {
			if (request.getParameter("startdate")!=null && (!request.getParameter("startdate").equals("mm/dd/yyyy")))
				startDate = OL_SEARCH_DATE_FORMAT.parse(request.getParameter("startdate"));
		} catch (java.text.ParseException e) {
			logger.warn(e);
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
		
		
		int pageIdx = 1;
		int pageSize = 20;
		
		if (request.getParameter("pageIdx") != null)
			pageIdx = Integer.parseInt(request.getParameter("pageIdx"));
		else
			pageIdx = pageNumber;
		
		if (request.getParameter("pageSize") != null)
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		else
			pageSize = this.pageSize;
		
		if (request.getParameter("format")!=null)
			this.format = request.getParameter("format").toLowerCase();
		
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
			if(sortField == null && (((term != null && term.contains("otype:bill")) 
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
			
			if(format.matches("(rss|atom)")) {
				pageSize = 1000;
				sortField = "modified";
			}
			
			request.setAttribute(OpenLegConstants.PAGE_IDX,pageIdx+"");
			request.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");
						
			if (term.length() == 0)	throw new ApiRequestException(
					TextFormatter.append("no term given"));
			
			String searchFormat = "json";
									
			if(term != null && !term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
				sr = SearchEngine.getInstance().search(queryBuilder.and().current().query(),
						searchFormat,start,pageSize,sortField,sortOrder);
			}
			else {
				sr = SearchEngine.getInstance().search(term,searchFormat,start,pageSize,sortField,sortOrder);
			}
					
			ApiHelper.buildSearchResultList(sr);
						
			if(sr == null || sr.getResults() == null || sr.getResults().isEmpty()) throw new ApiRequestException(
					TextFormatter.append("no results for query"));
			
			request.setAttribute("results", sr);
			
		} catch (Exception e) {
			logger.error("Search Error: " + request.getRequestURI(),e);
		}
	}
	
	@Override
	public String getView() {
		return TextFormatter.append("/views/search-", format, ".jsp");
	}
	
	@Override
	public boolean hasParameters() {
		return type!= null && term!=null;
	}
	
	private String whichTerm(String uriParam) {
		if(valid(uriParam))
			return uriParam;
		
		String search = request.getParameter("search");
		
		String term = request.getParameter("term");
		
		if(search != null) {
			request.setAttribute("search", search);
			term = search;
		}
				
		String tempTerm = null;
		if((tempTerm = BillCleaner.getDesiredBillNumber(term)) != null) {
			term = "oid:" + tempTerm;
		}
		
		return term;
	}
	
	private boolean valid(String str) {
		return str != null && str.length() > 0;
	}
	
	public enum SearchView implements ApiEnum {
		SEARCH		("search", Bill.class, new String[] {"atom", "csv", "html-list", 
														 "html", "json", "mobile", 
														 "rss", "xml"});
		
		public final String view;
		public final Class<? extends SenateObject> clazz;
		public final String[] formats;
		
		private SearchView(final String view, final Class<? extends SenateObject> clazz, final String[] formats) {
			this.view = view;
			this.clazz = clazz;
			this.formats = formats;
		}
		
		public String view() {
			return view;
		}
		public String[] formats() {
			return formats;
		}
		public Class<? extends SenateObject> clazz() {
			return clazz;
		}
	}
}
