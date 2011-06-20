package gov.nysenate.openleg.api.servlets;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.TextFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Access to multi-views by search key
 *
 */
public class KeyValueViewsServlet extends HttpServlet implements OpenLegConstants {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(KeyValueViewsServlet.class);
	
	private static final String SRV_DELIM = "/";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		if (req.getParameter("reset") != null) {
			SearchEngine.getInstance().closeSearcher();
			return;
		}

		/* bills, calendars, transcripts, meetings, sponsor, committee */
		String format = null;

		/*
		 * accepted types: bill, calendar, meeting, transcript as well as bills,
		 * calendars, meetings, transcripts, actions, votes
		 */
		
		String key;
		
		String value;

		String uri = java.net.URLDecoder.decode(req.getRequestURI(),
				OpenLegConstants.ENCODING);
		req.setAttribute(KEY_PATH, uri);

		logger.info(TextFormatter.append("request: ", uri,
				" (" + req.getRequestURI(), ")"));

		StringTokenizer st = new StringTokenizer(uri, SRV_DELIM);
		st.nextToken(); // legislation
		key = st.nextToken();
		value = st.nextToken();

		int pageIdx = DEFAULT_START_PAGE;
		int pageSize = DEFAULT_PAGE_SIZE;

		try {
			format = "html";

			if (req.getSession().getAttribute("mobile") != null)
				format = "mobile";

			if (st.hasMoreTokens()) {
				pageIdx = Integer.parseInt(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				pageSize = Integer.parseInt(st.nextToken());
			} else if (format.equals(FORMAT_XML)) // for now with XML
				pageSize = DEFAULT_API_PAGE_SIZE;

			doRequest(format, key, value, pageIdx, pageSize, req, resp);
		} catch (NumberFormatException nfe) {
			logger.warn("Invalid API call", nfe);
		} catch (NoSuchElementException nse) {
			logger.warn("Invalid API call", nse);
		} catch (NullPointerException npe) {
			logger.warn("Invalid API call", npe);
		} catch (Exception e) {
			logger.warn("Invalid API call", e);
		}
	}
	
	public void doRequest(String format, String key, String value, int pageIdx, 
			int pageSize, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String urlPath = TextFormatter.append("/legislation/", key, "/", value, "/");

		String viewPath = "";
		String sFormat = "json";
		String sortField = "sortindex";
		boolean sortOrder = false;
		
		QueryBuilder queryBuilder = new QueryBuilder();
		
		String filter = req.getParameter("filter");
		
		SenateResponse sr = null;

		if (pageSize > MAX_PAGE_SIZE)
			throw new ServletException("The maximum page size is "
					+ MAX_PAGE_SIZE);

		// now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;

		logger.info(TextFormatter.append("request: key=", key,";value=",value, ";format=",
				format, ";paging=", start, "/", end));
		try {
			
			queryBuilder.keyValue(key, value).and().current().and().active();
			if(filter != null) queryBuilder.and().insertAfter(filter);
			
			sr = SearchEngine.getInstance().search(queryBuilder.query(), sFormat,
					start, pageSize, sortField, sortOrder);

			logger.info(TextFormatter.append("got search results: ", sr.getResults().size()));

			if (sr.getResults().size() == 0) {
				resp.sendError(404);
				return;
			} 
			else {
				viewPath = TextFormatter.append("/views/", "search", "-", format, ".jsp");

				sr.setResults(ApiHelper.buildSearchResultList(sr));
				
				req.setAttribute("sortField", sortField);
				req.setAttribute("sortOrder", Boolean.toString(sortOrder));
				req.setAttribute("type", key);
				req.setAttribute("term", queryBuilder.query());
				req.setAttribute("format", format);
				req.setAttribute(PAGE_IDX, pageIdx + "");
				req.setAttribute(PAGE_SIZE, pageSize + "");
				req.setAttribute("urlPath", urlPath);
				req.setAttribute("results", sr);
			}
		} catch (Exception e) {
			logger.warn("search controller didn't work for: " + req.getRequestURI(), e);
		}

		try {
			logger.info("routing to search controller:" + viewPath);
			getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
		} catch (Exception e) {
			logger.warn("search controller didn't work for: " + req.getRequestURI(), e);
		}
	}
}
