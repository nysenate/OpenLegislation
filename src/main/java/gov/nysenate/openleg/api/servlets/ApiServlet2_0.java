package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest;
import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.SearchRequest2_0;
import gov.nysenate.openleg.api.SearchRequest2_0.SearchView2_0;
import gov.nysenate.openleg.api.SingleViewRequest2_0;
import gov.nysenate.openleg.api.SingleViewRequest2_0.SingleView2_0;
import gov.nysenate.openleg.api.servlets.ApiServlet.Join;
import gov.nysenate.openleg.util.OpenLegConstants;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ApiServlet2_0 extends HttpServlet implements OpenLegConstants {

    public static final int SINGLE_TYPE_2_0 = 1;
    public static final int SINGLE_ID_2_0 = 2;
    public static final int SINGLE_FORMAT_2_0 = 3;

    public static final int SEARCH_TYPE_2_0 = 1;
    public static final int SEARCH_FORMAT_2_0 = 2;

    /*
     * Used to match the start of a single, multi or key value view..
     * 		/legislation/2.0/[view type]
     * 		/legislation/api/2.0/[view type]
     */
    public static final String BASE_START_2_0 = "^(?i)/legislation/(?:api/)?2\\.0/(";

    /*
     * Ends base start, surrounds possible formats associated with a view
     */
    public static final String BASE_MIDDLE_2_0 = ")\\.(";

    public static final String BASE_END_2_0 = ")$";

    public static final String SINGLE_MIDDLE_2_0 = ")/(.+)\\.(";

    public final Pattern SINGLE_PATTERN_2_0;
    public final Pattern SEARCH_PATTERN_2_0;

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ApiServlet2_0.class);

    public ApiServlet2_0() throws ServletException {
        super();

        String singleViews2_0 = new Join<SingleView2_0>() {
            @Override
            public String value(SingleView2_0 t) {
                return t.view;
            }
        }.join(SingleView2_0.values(), "|");

        String singleFormats2_0 = new Join<String>() {
            @Override
            public String value(String t) {
                return t;
            }
        }.join(AbstractApiRequest.getUniqueFormats(SingleView2_0.values()), "|");

        String searchViews2_0 = new Join<SearchView2_0>() {
            @Override
            public String value(SearchView2_0 t) {
                return t.view;
            }
        }.join(SearchView2_0.values(), "|");

        String searchFormats2_0 = new Join<String>() {
            @Override
            public String value(String t) {
                return t;
            }
        }.join(AbstractApiRequest.getUniqueFormats(SearchView2_0.values()), "|");

        SINGLE_PATTERN_2_0 = Pattern.compile(
                TextFormatter.append(
                        BASE_START_2_0,singleViews2_0,SINGLE_MIDDLE_2_0,singleFormats2_0,BASE_END_2_0)
                );
        logger.info(TextFormatter.append("Single View pattern (2.0) generated: ", SINGLE_PATTERN_2_0.pattern()));

        SEARCH_PATTERN_2_0 = Pattern.compile(
                TextFormatter.append(
                        BASE_START_2_0,searchViews2_0,BASE_MIDDLE_2_0,searchFormats2_0,BASE_END_2_0)
                );
        logger.info(TextFormatter.append("Search View pattern (2.0) generated: ", SEARCH_PATTERN_2_0.pattern()));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
        Matcher m = null;

        String uri = URLDecoder.decode(request.getRequestURI(), ENCODING);

        AbstractApiRequest apiRequest = null;

        if(apiRequest == null && (m = SINGLE_PATTERN_2_0.matcher(uri)) != null && m.find()) {
            logger.info(TextFormatter.append("Single request: ", uri));

            apiRequest = new SingleViewRequest2_0(	request,
                    response,
                    m.group(SINGLE_FORMAT_2_0),
                    m.group(SINGLE_TYPE_2_0),
                    m.group(SINGLE_ID_2_0));
        }

        if(apiRequest == null && (m = SEARCH_PATTERN_2_0.matcher(uri)) != null && m.find()) {
            logger.info(TextFormatter.append("Search request: ", uri));

            apiRequest = new SearchRequest2_0(	request,
                    response,
                    m.group(SEARCH_FORMAT_2_0),
                    m.group(SEARCH_TYPE_2_0),
                    request.getParameter("term"));
        }

        try {
            if(apiRequest == null) throw new ApiRequestException(
                    TextFormatter.append("Failed to route request: ", uri));

            apiRequest.execute();
        }
        catch(ApiRequestException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
