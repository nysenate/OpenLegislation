package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.converter.Api2JsonConverter;
import gov.nysenate.openleg.converter.Api2XmlConverter;
import gov.nysenate.openleg.converter.pdf.PDFConverter;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class ApiServlet2 extends HttpServlet
{
    public static int DEFAULT_PAGE_SIZE = 20;
    public static int MAX_PAGE_SIZE = 1000;
    public final Logger logger = Logger.getLogger(ApiServlet2.class);
    public final static Pattern documentPattern = Pattern.compile("(?:/api)?/2.0/(vote|action|bill|calendar|meeting|transcript)/(.*)?\\.(json|jsonp|xml|pdf)$");
    public final static Pattern searchPattern = Pattern.compile("(?:/api)?/2.0/(search|votes|bills|meetings|actions|calendars|transcripts).(json|jsonp|xml)$");

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageIdx = 1;
        int pageSize = DEFAULT_PAGE_SIZE;
        boolean sortOrder = false;
        String uri = request.getRequestURI();
        String path = request.getServletPath()+(request.getPathInfo() != null ? request.getPathInfo() : "");
        String sort = request.getParameter("sort");
        String pageIdxParam = request.getParameter("pageIdx");
        String pageSizeParam = request.getParameter("pageSize");
        String sortOrderParam = request.getParameter("sortOrder");

        try {
            try {
                if (pageIdxParam != null) {
                    pageIdx = Integer.parseInt(pageIdxParam);
                    if (pageIdx < 1) {
                        throw new ApiRequestException("Page number must be greater than 0");
                    }
                }

                if (pageSizeParam != null) {
                    pageSize = Integer.parseInt(pageSizeParam);
                    if (pageSize > MAX_PAGE_SIZE) {
                        throw new ApiRequestException("Page size must be less than 1000");
                    }
                    else if (pageSize < 1) {
                        throw new ApiRequestException("Page size must be greater than 0");
                    }
                }
            }
            catch (NumberFormatException e) {
                throw new ApiRequestException("Invalid pageIdx ["+pageIdxParam+"] or pageSize ["+pageSizeParam+"]. Must be an integer.");
            }

            if ("true".equals(sortOrderParam)) {
                sortOrder = true;
            }
            else if (sortOrderParam == null || "false".equals(sortOrderParam)) {
                sortOrder = false;
            }
            else {
                throw new ApiRequestException("Invalid sortOrder parameter: "+sortOrderParam);
            }

            Matcher searchMatcher = searchPattern.matcher(path);
            Matcher documentMatcher = documentPattern.matcher(path);
            if (searchMatcher.find()) {
                String type = searchMatcher.group(1);
                String format = searchMatcher.group(2);
                String term = request.getParameter("term");

                if (!type.equals("search")) {
                    term = "otype:"+type.substring(0, type.length()-1)+(term == null ? "" : " AND "+term);
                }

                doSearch(request, response, format, type, term, pageIdx, pageSize, sort, sortOrder);
            }
            else if (documentMatcher.find()) {
                String otype = documentMatcher.group(1);
                String oid = documentMatcher.group(2);
                String format = documentMatcher.group(3);
                doSingleView(request, response, format, otype, oid);
            }
            else {
                throw new ApiRequestException("Invalid request: "+uri);
            }
        }
        catch (ApiRequestException e) {
            logger.error(e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void doSearch(HttpServletRequest request, HttpServletResponse response, String format, String type, String term, int pageNumber, int pageSize, String sort, boolean sortOrder) throws ApiRequestException
    {
        try {
            int start = (pageNumber-1) * pageSize;
            SenateResponse sr = Application.getLucene().search(term, start, pageSize, sort, sortOrder);
            ApiHelper.buildSearchResultList(sr);

            if (format.equals("json")) {
                response.setContentType("application/json");
                new Api2JsonConverter().write(sr, response.getOutputStream());
            }
            else if (format.equals("jsonp")) {
                String callback = request.getParameter("callback");
                if (callback != null && callback != "") {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/javascript");
                    out.write(callback+"("+new Api2JsonConverter().toString(sr)+");");
                }
                else {
                    throw new ApiRequestException("callback parameter required for jsonp queries.");
                }
            }
            else if (format.equals("xml")) {
                response.setContentType("application/xml");
                new Api2XmlConverter().write(sr, response.getOutputStream());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiRequestException("internal server error.");
        }
    }

    private void doSingleView(HttpServletRequest request, HttpServletResponse response, String format, String type, String id) throws ApiRequestException
    {
        try {
            SenateResponse sr = Application.getLucene().search("otype:"+type+" AND oid:\""+id+"\"", 0, 1, null, false);
            ApiHelper.buildSearchResultList(sr);

            if (format.equals("json")) {
                response.setContentType("application/json");
                // Writing directly to the output stream here currently makes UTF8 errors.
                response.getWriter().write(new Api2JsonConverter().toString(sr));
            }
            else if (format.equals("jsonp")) {
                String callback = request.getParameter("callback");
                if (callback != null && callback != "") {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/javascript");
                    out.write(callback+"("+new Api2JsonConverter().toString(sr)+");");
                }
                else {
                    throw new ApiRequestException("callback parameter required for jsonp queries.");
                }
            }
            else if (format.equals("xml")) {
                response.setContentType("application/xml");
                new Api2XmlConverter().write(sr, response.getOutputStream());
            }
            else if (format.equals("pdf")) {
                if (sr.getResults().size() == 0) {
                    throw new ApiRequestException("No matching document could be found.");
                }

                response.setContentType("application/pdf");
                PDFConverter.write(sr.getResults().get(0).object, response.getOutputStream());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiRequestException("internal server error.", e);
        }
    }
}
