package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.SearchRequest.SearchView;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.OpenLegConstants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class SearchRequest2_0 extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SearchRequest.class);

    String type;
    String term;

    public SearchRequest2_0(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String term) {
        super(request, response, null, null, format, getApiEnum(SearchView.values(),type));
        this.type = type;
        try {
            this.term = term == null ? null : URLDecoder.decode(term,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        String vFormat = format.equals("jsonp") ? "json" : format;
        request.setAttribute("format", vFormat);

        int pageSize = super.pageSize;
        if (request.getParameter("pageSize") != null) {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        }

        pageNumber = super.pageNumber;
        if (request.getParameter("pageIdx") != null) {
            pageNumber = Integer.parseInt(request.getParameter("pageIdx"));
        }

        Boolean sortOrder = Boolean.parseBoolean(request.getParameter("sortOrder"));
        String sortField = request.getParameter("sort");

        if(sortField == null) {
            sortField = "modified";
            sortOrder = true;
        }

        request.setAttribute("sortField",sortField);
        request.setAttribute("sortOrder",sortOrder);

        request.setAttribute(OpenLegConstants.PAGE_IDX,pageNumber+"");
        request.setAttribute(OpenLegConstants.PAGE_SIZE,pageSize+"");

        int start = (pageNumber - 1) * pageSize;

        try {
            SenateResponse sr = SearchEngine.getInstance().search(ApiHelper.dateReplace(term), vFormat, start, pageSize, null, false);
            request.setAttribute("results", sr);
        }
        catch (ParseException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public String getView() {
        return "/views2/v2-api.jsp";
    }

    @Override
    public boolean hasParameters() {
        return type!= null && term!=null;
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

    public enum SearchView2_0 implements ApiEnum {
        SEARCH		("search", Bill.class, new String[] {"json", "jsonp", "xml"});

        public final String view;
        public final Class<? extends SenateObject> clazz;
        public final String[] formats;

        private SearchView2_0(final String view, final Class<? extends SenateObject> clazz, final String[] formats) {
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
