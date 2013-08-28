package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Api1JsonConverter;
import gov.nysenate.openleg.util.Api1XmlConverter;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.RequestUtils;
import gov.nysenate.openleg.util.SessionYear;
import gov.nysenate.openleg.util.TextFormatter;

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
public class ApiServlet1 extends HttpServlet
{
    public static int MAX_PAGE_SIZE = 1000;

    public final Logger logger = Logger.getLogger(ApiServlet1.class);
    public final static Pattern documentPattern = Pattern.compile("(:?/api)?/1.0/(json|xml|jsonp)/(bill|calendar|meeting|transcript)/(.*)?\\.(json|jsonp|xml)");
    public final static Pattern searchPattern = Pattern.compile("(?:/api)?/1.0/(json|xml|jsonp)/(search|votes|bills|meetings|actions|calendar|transcripts)/?");

    public static void main(String[] args)
    {
        System.out.println(searchPattern.matcher("/api/1.0/xml/meetings").find());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageIdx = 1;
        int pageSize = 20;
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
                    if (pageIdx > MAX_PAGE_SIZE) {
                        throw new ApiRequestException("Page size must be less than 1000");
                    }
                }

                if (pageSizeParam != null) {
                    pageSize = Integer.parseInt(pageSizeParam);
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

            System.out.println(path);
            Matcher searchMatcher = searchPattern.matcher(path);
            Matcher documentMatcher = documentPattern.matcher(path);
            if (searchMatcher.find()) {
                String format = searchMatcher.group(1);
                String type = searchMatcher.group(2);
                String term = RequestUtils.getSearchString(request);
                if (!type.equals("search")) {
                    term += "otype:"+type.substring(0, type.length()-1)+" "+term;
                }
                else if (term.isEmpty()) {
                    throw new ApiRequestException("A search term is required.");
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

    private void doSearch(HttpServletRequest request, HttpServletResponse response, String format, String type, String term, int pageNumber, int pageSize, String sort, boolean sortOrder) throws ApiRequestException, IOException
    {
        // Verify that all bills in the query are in the proper format
        term = Bill.formatBillNo(term);

        // If we aren't requesting a specific document or time period, only show current documents
        if(!term.contains("year:") && !term.contains("when:") && !term.contains("oid:")) {
            term += " AND year:"+SessionYear.getSessionYear();
        }

        // Only show inactive documents when they search by oid, but don't repeat yourself
        if (!term.contains("oid:") && !term.contains("active:")) {
            term += " AND active:true";
        }

        try {
            int start = (pageNumber - 1) * pageSize;
            SenateResponse sr = Application.getLucene().search(term,"json",start,pageSize, sort, sortOrder);
            ApiHelper.buildSearchResultList(sr);

            if (format.equals("json")) {
                response.setContentType("application/json");
                new Api1JsonConverter().write(sr, response.getOutputStream());
            }
            else if (format.equals("jsonp")) {
                String callback = request.getParameter("callback");
                if (callback != null && callback != "") {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/javascript");
                    out.write(callback+"("+new Api1JsonConverter().toString(sr)+");");
                }
                else {
                    throw new ApiRequestException("callback parameter required for jsonp queries.");
                }
            }
            else if (format.equals("xml")) {
                response.setContentType("application/xml");
                new Api1XmlConverter().write(sr, response.getOutputStream());
            }

        } catch (Exception e) {
            logger.error(e);
            throw new ApiRequestException("internal server error.");
        }
    }

    private void doSingleView(HttpServletRequest request, HttpServletResponse response, String format, String type, String id) throws ApiRequestException, IOException
    {
        BaseObject object = (BaseObject)Application.getLucene().getSenateObject(id, type);

        if(object == null) {
            throw new ApiRequestException(TextFormatter.append("couldn't find id: ", id, " of type: ", type));
        }
        else {
            if (format.equals("json")) {
                response.setContentType("application/json");
                new Api1JsonConverter().write(object, response.getOutputStream());
            }
            else if (format.equals("jsonp")) {
                String callback = request.getParameter("callback");
                if (callback != null && callback != "") {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/javascript");
                    out.write(callback+"("+new Api1JsonConverter().toString(object)+");");
                }
                else {
                    throw new ApiRequestException("callback parameter required for jsonp queries.");
                }
            }
            else if (format.equals("xml")) {
                response.setContentType("application/xml");
                new Api1XmlConverter().write(object, response.getOutputStream());
            }
        }
    }
}
