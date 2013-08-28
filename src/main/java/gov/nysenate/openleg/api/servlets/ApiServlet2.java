package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Application;
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
public class ApiServlet2 extends HttpServlet
{
    public final Logger logger = Logger.getLogger(ApiServlet2.class);
    public final static Pattern documentPattern = Pattern.compile("(?:/api)?/2.0/(bill|calendar|meeting|transcript)/(.*)?\\.(json|jsonp|xml)");
    public final static Pattern searchPattern = Pattern.compile("(?:/api)?/2.0/search.(json|jsonp|xml)");

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
                doSearch(request, response, format, "search", term, pageIdx, pageSize, sort, sortOrder);
            }
            else if (documentMatcher.find()) {
                String otype = documentMatcher.group(1);
                String oid = documentMatcher.group(2);
                String format = documentMatcher.group(3);
                doSingleView(request, response, format, otype, oid);
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

    private void doSearch(HttpServletRequest request, HttpServletResponse response, String format, String type, String term, int pageNumber, int pageSize, String sort, boolean sortOrder) throws ApiRequestException
    {
        try {
            int start = (pageNumber - 1) * pageSize;
            SenateResponse sr = Application.getLucene().search(term,"json",start,pageSize, sort, sortOrder);
            ApiHelper.buildSearchResultList(sr);

            if (format.equals("json")) {
                response.setContentType("application/json");
                //new Api2JsonConverter().write(sr, response.getOutputStream());
            }
            else if (format.equals("jsonp")) {
                String callback = request.getParameter("callback");
                if (callback != null && callback != "") {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/javascript");
                    //out.write(callback+"("+new Api2JsonConverter().toString(sr)+");");
                }
                else {
                    throw new ApiRequestException("callback parameter required for jsonp queries.");
                }
            }
            else if (format.equals("xml")) {
                response.setContentType("application/xml");
                //new Api2XmlConverter().write(sr, response.getOutputStream());
            }

        } catch (Exception e) {
            logger.error(e);
            throw new ApiRequestException("internal server error.");
        }
    }

    private void doSingleView(HttpServletRequest request, HttpServletResponse response, String format, String type, String id) throws ApiRequestException
    {
        try {
            BaseObject object = (BaseObject)Application.getLucene().getSenateObject(id, type);

            if(object == null) {
                throw new ApiRequestException(TextFormatter.append("couldn't find id: ", id, " of type: ", type));
            }
            else {
                if (format.equals("json")) {
                    response.setContentType("application/json");
                    //new Api2JsonConverter().write(object, response.getOutputStream());
                }
                else if (format.equals("jsonp")) {
                    String callback = request.getParameter("callback");
                    if (callback != null && callback != "") {
                        PrintWriter out = response.getWriter();
                        response.setContentType("application/javascript");
                        //out.write(callback+"("+new Api2JsonConverter().toString(object)+");");
                    }
                    else {
                        throw new ApiRequestException("callback parameter required for jsonp queries.");
                    }
                }
                else if (format.equals("xml")) {
                    response.setContentType("application/xml");
                    //new Api2XmlConverter().write(object, response.getOutputStream());
                }
            }

        } catch (Exception e) {
            logger.error(e);
            throw new ApiRequestException("internal server error.");
        }
    }
}
