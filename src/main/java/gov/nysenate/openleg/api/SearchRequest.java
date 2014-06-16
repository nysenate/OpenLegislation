package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.JSPHelper;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SearchRequest extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SearchRequest.class);

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    String type;
    String term;

    public SearchRequest(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String term, String pageNumber, String pageSize) {
        super(request, response, pageNumber, pageSize, format, getApiEnum(SearchView.values(),type));
        logger.info("New search request: format="+format+", type="+type+", term="+term+", page="+pageNumber+", size="+pageSize);
        this.type = type;
        try {
            this.term = whichTerm(term);
            this.term = URLDecoder.decode(this.term,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported encoding UTF-8", e);
        } catch (IllegalArgumentException e) {
            logger.error("Malformed term: "+this.term, e);
        }
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        QueryBuilder queryBuilder = new QueryBuilder();
        try {
            // If the search terms sent are valid, use them
            if (valid(term)) {
                queryBuilder.insertBefore(term);
            }

            // If we have start and end dates - construct a range between them
            // If we have start date only - restrict the range to that day only
            // If we don't have start date - don't do anything
            // If either date is malformed, pretend the user never sent dates
            Date startDate = null;
            Date endDate =  null;
            try {
                String endDateParam = request.getParameter("enddate");
                String startDateParam = request.getParameter("startdate");
                if (valid(startDateParam) && !startDateParam.equals("mm/dd/yyyy")) {
                    startDate = dateFormat.parse(startDateParam+" 00:00:00");

                    if (valid(endDateParam) && !endDateParam.equals("mm/dd/yyyy")) {
                        endDate = dateFormat.parse(endDateParam+" 23:59:59");
                    }
                    else {
                        endDate = dateFormat.parse(startDateParam+" 23:59:59");
                    }

                    queryBuilder.and().range("when",Long.toString(startDate.getTime()), Long.toString(endDate.getTime()));
                }
            } catch (java.text.ParseException e) {
                logger.warn("Invalid date format", e);
                throw new ApiRequestException("Invalid date format", e);
            }

            // One of 2009, 2011, 2013, etc
            String session = request.getParameter("session");
            if(valid(session)) {
                queryBuilder.and().inSession(Integer.parseInt(session));
            }

            // Currently full-text search ~= osearch (most fields) plus the bill text (where applicable)
            String full = request.getParameter("full");
            if (valid(full)) {
                queryBuilder.and().append(" (").keyValue("full", full, "(", ")").or().keyValue("osearch", full, "(", ")").append(") ");
            }

            // Wrap in ( ) so that the user can use boolean logic if they want
            String memo = request.getParameter("memo");
            if (valid(memo)) {
                queryBuilder.and().keyValue("memo", memo, "(", ")");
            }

            String status = request.getParameter("status");
            if (valid(status)) {
                queryBuilder.and().keyValue("status", status, "(", ")");
            }

            String sameas = request.getParameter("sameas");
            if (valid(sameas)) {
                queryBuilder.and().keyValue("sameas", sameas, "(", ")");
            }

            String[] sponsors = request.getParameterValues("sponsor");
            if (sponsors != null) {
                queryBuilder.and().keyValue("sponsor", StringUtils.join(sponsors, " "), "(", ")");
            }

            String[] cosponsors = request.getParameterValues("cosponsors");
            if (cosponsors != null) {
                queryBuilder.and().keyValue("cosponsors", StringUtils.join(cosponsors, " "), "(", ")");
            }

            String[] committee = request.getParameterValues("committee");
            if (committee != null) {
                queryBuilder.and().keyValue("committee", StringUtils.join(committee, " "), "(", ")");
            }

            String location = request.getParameter("location");
            if (valid(location)) {
                queryBuilder.and().keyValue("location", location, "(", ")");
            }

            // If we aren't requesting a specific document or time period, only show current documents
            term = queryBuilder.query();
            if(term != null && !term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
                term = queryBuilder.and().current().query();
            }

            // Only show inactive documents when they search by oid, but don't repeat yourself
            if (!term.contains("oid:") && !term.contains("active:")) {
                term = queryBuilder.and().active().query();
            }
        }
        catch (QueryBuilderException e) {
            logger.error("Invalid query construction", e);
            throw new ApiRequestException("Invalid query construction", e);
        }

        // Cut this short if we've got no query
        if (term.length() == 0) {
            throw new ApiRequestException(TextFormatter.append("no term given"));
        }

        // Verify that all bills in the query are in the proper format
        term = Bill.formatBillNo(term);

        // One of bill, meeting, transcript, vote, etc
        String type = request.getParameter("type");

        // null represents default sort order
        String sortOrderParam = request.getParameter("sortOrder");
        if (!valid(sortOrderParam)) {
            sortOrderParam = null;
        }
        boolean sortOrder = Boolean.parseBoolean(sortOrderParam);

        // null represents sort by relevance
        String sortField = request.getParameter("sort");
        if (!valid(sortField)) {
            sortField = null;
        }

        if (request.getParameter("format")!=null) {
            format = request.getParameter("format").toLowerCase();
        }

        // Override user options for rss and atom
        // TODO: Don't do anything for a CSV, what? Why not?
        if(format.matches("(rss|atom)")) {
            pageSize = 50;
            sortField = "modified";
            sortOrder = true;
        }
        else if(format.equalsIgnoreCase("csv")) {
            return;
        }

        // Pagination variables
        if (request.getParameter("pageIdx") != null) {
            pageNumber = Integer.parseInt(request.getParameter("pageIdx"));
        }

        if (request.getParameter("pageSize") != null) {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        }

        SenateResponse sr = null;
        try {
            int start = (pageNumber - 1) * pageSize;
            sr = Application.getLucene().search(term, start, pageSize, sortField, sortOrder);
            if((sr.getResults() == null || sr.getResults().isEmpty()) && this.format.contains("html")) {
                throw new ApiRequestException(TextFormatter.append("no results for query"));
            }
            ApiHelper.buildSearchResultList(sr);
        }
        catch (IOException e) {
            logger.error(e);
        }

        request.setAttribute("term", term);
        request.setAttribute("type", type);
        request.setAttribute("sortOrder", Boolean.toString(sortOrder));
        request.setAttribute("sortField", sortField);
        request.setAttribute(OpenLegConstants.PAGE_IDX, pageNumber);
        request.setAttribute(OpenLegConstants.PAGE_SIZE, pageSize);
        request.setAttribute("results", sr);
        HashMap<String, String> feeds = new HashMap<String, String>();
        feeds.put(term, JSPHelper.getFullLink(request, "/search/?format=atom&amp;term="+term));
        request.setAttribute("feeds", feeds);
    }

    @Override
    public String getView() {
        String vFormat = format.equals("jsonp") ? "json" : format;
        return TextFormatter.append("/views/search-", vFormat, ".jsp");
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
        String type = request.getParameter("type");

        if(valid(search)) {
            request.setAttribute("search", search);
            term = search;
        }

        if(valid(type)) {
            term = "otype:" + type;
        }

        String tempTerm = null;
        if((tempTerm = getDesiredBillNumber(term)) != null) {
            term = "oid:" + tempTerm;
        }

        return (term == null) ? "" : term;
    }

    private boolean valid(String str) {
        return str != null && !str.isEmpty();
    }

    /*
     * on search this attempts to format a bill id based on
     * what version the bill is at and returns the 'desired'
     * result.  this mimics lrs functionality
     *
     * if s1234, s1234a and s1234b exist:
     *
     * s1234a -> S1234A-2011
     * s1234- -> S1234-2011
     * s1234  -> S1234B-2011
     *
     */
    public String getDesiredBillNumber(String term) {
        if(term == null) return null;

        String billNo = Bill.formatBillNo(term);

        if(billNo.matches("(?i)[sajr]\\d+\\w?\\-\\d{4}")) {
            if(term.matches(".+?(\\-|[a-zA-Z])")) {
                return billNo;
            }

            Bill newestAmendment = Application.getLucene().getNewestAmendment(billNo);
            if(newestAmendment != null) {
                return newestAmendment.getBillId();
            }
        }
        return null;
    }

    public enum SearchView implements ApiEnum {
        SEARCH		("search", Bill.class, new String[] {"atom", "csv", "html-list",
            "html", "json", "jsonp", "mobile",
            "rss", "xml"});

        public final String view;
        public final Class<? extends BaseObject> clazz;
        public final String[] formats;

        private SearchView(final String view, final Class<? extends BaseObject> clazz, final String[] formats) {
            this.view = view;
            this.clazz = clazz;
            this.formats = formats;
        }

        @Override
        public String view() {
            return view;
        }
        @Override
        public String[] formats() {
            return formats;
        }
        @Override
        public Class<? extends BaseObject> clazz() {
            return clazz;
        }
    }
}
