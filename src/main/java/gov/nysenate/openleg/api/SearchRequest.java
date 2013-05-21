package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class SearchRequest extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SearchRequest.class);

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    String type;
    String term;

    public SearchRequest(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String term, String pageNumber, String pageSize) {
        super(request, response, pageNumber, pageSize, format, getApiEnum(SearchView.values(),type));
        this.type = type;
        try {
            term = whichTerm(term);

            if(term == null) term = "";

            this.term = URLDecoder.decode(term,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
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
                logger.warn(e);
            }

            // One of 2009, 2011, 2013, etc
            String session = request.getParameter("session");
            if(valid(session)) {
                queryBuilder.and().searchValue("year", session);
            }

            // TODO: Why does this also search in the osearch field in addition to the text?
            String full = request.getParameter("full");
            if (valid(full)) {
                queryBuilder.and().append(" (").searchValue("full", full).or().searchValue("osearch", full).append(") ");
            }

            // Wrap in ( ) so that the user can use boolean logic if they want
            String memo = request.getParameter("memo");
            if (valid(memo)) {
                queryBuilder.and().searchValue("memo", memo);
            }

            String status = request.getParameter("status");
            if (valid(status)) {
                queryBuilder.and().searchValue("status", status);
            }

            String sponsor = request.getParameter("sponsor");
            if (valid(sponsor)) {
                queryBuilder.and().searchValue("sponsor", sponsor);
            }

            String cosponsors = request.getParameter("cosponsors");
            if (valid(cosponsors)) {
                queryBuilder.and().searchValue("cosponsors", cosponsors);
            }

            String sameas = request.getParameter("sameas");
            if (valid(sameas)) {
                queryBuilder.and().searchValue("sameas", sameas);
            }

            String committee = request.getParameter("committee");
            if (valid(committee)) {
                queryBuilder.and().searchValue("committee", committee);
            }

            String location = request.getParameter("location");
            if (valid(location)) {
                queryBuilder.and().searchValue("location", location);
            }

            // If we aren't requesting a specific document or time period, only show active documents
            term = queryBuilder.query();
            if(term != null && !term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
                term = queryBuilder.and().current().query();
            }
        }
        catch (QueryBuilderException e) {
            logger.error("Bad query Build.", e);
        }

        // Cut this short if we've got no query
        if (term.length() == 0) {
            throw new ApiRequestException(TextFormatter.append("no term given"));
        }

        // Verify that all bills in the query are in the proper format
        term = Bill.formatBillNo(term);

        // One of bill, meeting, transcript, vote, etc
        String type = request.getParameter("type");

        // Bill searches can have different default values.
        boolean isBillSearch = term != null && term.contains("otype:bill") || (type != null && type.equals("bill"));

        // If sortOrder is not specified then use false for bills true for everything else
        String sortOrderParam = request.getParameter("sortOrder");
        if (!valid(sortOrderParam)) {
            sortOrderParam = isBillSearch ? "false" : "true";
        }
        boolean sortOrder = Boolean.parseBoolean(sortOrderParam);

        // If a sortField is not specified then use:
        //   * sortindex - for bills (TODO: what is this?)
        //   * when - for everything else
        String sortField = request.getParameter("sort");
        if (!valid(sortField)) {
            sortField = isBillSearch ? "sortindex" : "when";
        }

        if (request.getParameter("format")!=null) {
            format = request.getParameter("format").toLowerCase();
        }

        // Override user options for rss and atom
        // TODO: Don't do anything for a CSV, what? Why not?
        if(format.matches("(rss|atom)")) {
            pageSize = 1000;
            sortField = "modified";
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
            String searchFormat = "json";
            int start = (pageNumber - 1) * pageSize;
            sr = SearchEngine.getInstance().search(term,searchFormat,start,pageSize,sortField,sortOrder);
            if((sr.getResults() == null || sr.getResults().isEmpty()) && this.format.contains("html")) {
                throw new ApiRequestException(TextFormatter.append("no results for query"));
            }
            ApiHelper.buildSearchResultList(sr);
        } catch (ParseException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }

        request.setAttribute("term", term);
        request.setAttribute("type", type);
        request.setAttribute("sortOrder", Boolean.toString(sortOrder));
        request.setAttribute("sortField", sortField);
        request.setAttribute(OpenLegConstants.PAGE_IDX,String.valueOf(pageNumber));
        request.setAttribute(OpenLegConstants.PAGE_SIZE,String.valueOf(pageSize));
        request.setAttribute("results", sr);
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
        if((tempTerm = getDesiredBillNumber(term, SearchEngine.getInstance())) != null) {
            term = "oid:" + tempTerm;
        }

        return term;
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
    public String getDesiredBillNumber(String term, SearchEngine searchEngine) {
        if(term == null) return null;

        String billNo = Bill.formatBillNo(term);

        if(billNo.matches("(?i)[sajr]\\d+\\w?\\-\\d{4}")) {
            if(term.matches(".+?(\\-|[a-zA-Z])")) {
                return billNo;
            }

            Bill newestAmendment = searchEngine.getNewestAmendment(billNo);
            if(newestAmendment != null) {
                return newestAmendment.getSenateBillNo();
            }
        }
        return null;
    }

    public enum SearchView implements ApiEnum {
        SEARCH		("search", Bill.class, new String[] {"atom", "csv", "html-list",
            "html", "json", "jsonp", "mobile",
            "rss", "xml"});

        public final String view;
        public final Class<? extends SenateObject> clazz;
        public final String[] formats;

        private SearchView(final String view, final Class<? extends SenateObject> clazz, final String[] formats) {
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
        public Class<? extends SenateObject> clazz() {
            return clazz;
        }
    }
}
