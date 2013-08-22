package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.SearchRequest1;
import gov.nysenate.openleg.api.SingleViewRequest1;
import gov.nysenate.openleg.util.OpenLegConstants;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class ApiServlet1 extends HttpServlet implements OpenLegConstants
{
    public final Logger logger = Logger.getLogger(ApiServlet1.class);
    public final static Pattern documentPattern = Pattern.compile("(:?/api)?/1.0/(json|xml|jsonp)/(bill|calendar|meeting|transcript)/(.*)?\\.(json|jsonp|xml)");
    public final static Pattern searchPattern = Pattern.compile("(?:/api)?/1.0/(json|xml|jsonp)/(search|votes|bills|meetings|actions|calendar|transcripts)/?");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageIdx = 0;
        int pageSize = 0;
        boolean sortOrder = false;
        String uri = request.getRequestURI();
        String path = request.getServletPath();
        String sort = request.getParameter("sort");
        String pageIdxParam = request.getParameter("pageIdx");
        String pageSizeParam = request.getParameter("pageSize");
        String sortOrderParam = request.getParameter("sortOrder");

        try {
            try {
                pageIdx = Integer.parseInt(pageIdxParam);
                pageSize = Integer.parseInt(pageSizeParam);
                if ("true".equals(sortOrderParam)) {
                    sortOrder = true;
                }
                else if ("false".equals(sortOrderParam)) {
                    sortOrder = false;
                }
                else {
                    throw new ApiRequestException("Invalid sortOrder parameter: "+sortOrderParam);
                }
            }
            catch (NumberFormatException e) {
                throw new ApiRequestException("Invalid pageIdx ["+pageIdxParam+"] or pageSize ["+pageSizeParam+"]. Must be an integer.");
            }

            Matcher searchMatcher = searchPattern.matcher(path);
            Matcher documentMatcher = documentPattern.matcher(path);
            if (searchMatcher.find()) {
                String format = searchMatcher.group(1);
                String term = request.getParameter("term");
                new SearchRequest1(request, response, format, "search", term, pageIdx, pageSize, sort, sortOrder).execute();
            }
            else if (documentMatcher.find()) {
                String otype = documentMatcher.group(1);
                String oid = documentMatcher.group(2);
                String format = documentMatcher.group(3);
                new SingleViewRequest1(request, response, format, otype, oid, pageIdx, pageSize, sort, sortOrder).execute();
            }
            else {
                throw new ApiRequestException("Invalid requeset: "+uri);
            }
        }
        catch (ApiRequestException e) {
            logger.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
