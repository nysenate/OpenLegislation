package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * Access to SenateObject multi-views
 *
 */
public class SenateObjectViewsServlet extends HttpServlet implements OpenLegConstants {

	private static final long serialVersionUID = -7567155903739799800L;

	private static Logger logger = Logger.getLogger(SenateObjectViewsServlet.class);

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
		String type = null;

		String uri = java.net.URLDecoder.decode(req.getRequestURI(),
				OpenLegConstants.ENCODING);
		req.setAttribute(KEY_PATH, uri);

		logger.info(TextFormatter.append("request: ", uri,
				" (" + req.getRequestURI(), ")"));

		StringTokenizer st = new StringTokenizer(uri, SRV_DELIM);
		st.nextToken(); // legislation
		type = st.nextToken();

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

			doRequest(format, type, pageIdx, pageSize, req, resp);
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

	public void doRequest(String format, String type, int pageIdx,
			int pageSize, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String urlPath = TextFormatter.append("/legislation/", type, "/");

		String viewPath = "";
		String searchString = "";
		String sFormat = "json";
		String sortField = "when";
		boolean sortOrder = true;

		if (type.contains("bill")) {
			sortField = "sortindex";
			sortOrder = false;
		} else if (type.contains("meeting")) {
			sortField = "sortindex";
		}

		SenateResponse sr = null;

		if (pageSize > MAX_PAGE_SIZE)
			throw new ServletException("The maximum page size is "
					+ MAX_PAGE_SIZE);

		// now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;

		logger.info(TextFormatter.append("request: type=", type, ";format=",
				format, ";paging=", start, "/", end));
		try {
			type = type.substring(0, type.length() - 1);

			searchString = TextFormatter.append("otype:", type);

			/*
			 * generate default views query, filter by session year, documents
			 * outside of this range can be manually searched
			 */
			searchString = TextFormatter.append(
					ApiHelper.dateReplace(searchString), " AND (", "year:",
					SessionYear.getSessionYear(), " OR when:[",
					SessionYear.getSessionStart(), " TO ",
					SessionYear.getSessionEnd(), "]", ")", " AND active:",
					LUCENE_ACTIVE);
			
			sr = SearchEngine.getInstance().search(searchString, sFormat,
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
				req.setAttribute("type", type);
				req.setAttribute("term", searchString);
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
