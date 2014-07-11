package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.converter.Api1JsonConverter;
import gov.nysenate.openleg.converter.Api1XmlConverter;
import gov.nysenate.openleg.converter.pdf.BillTextPDFConverter;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateResponse;
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
import org.apache.pdfbox.exceptions.COSVisitorException;

@SuppressWarnings("serial")
public class ApiServlet1 extends HttpServlet
{
    public static int MAX_PAGE_SIZE = 1000;
    public static int DEFAULT_PAGE_SIZE = 20;

    public final Logger logger = Logger.getLogger(ApiServlet1.class);
    public final static Pattern documentPattern = Pattern.compile("(?:/api)?(?:/1.0)?/(json|xml|jsonp|html-print|lrs-print|html|pdf)/(bill|calendar|meeting|transcript)/(.*)$", Pattern.CASE_INSENSITIVE);
    public final static Pattern searchPattern = Pattern.compile("(?:/api)?(?:/1.0)?/(csv|atom|rss|json|xml|jsonp)/(search|votes|bills|meetings|actions|calendars|transcripts|sponsor)(?:/(.*?[a-z].*?))?(?:/([0-9]+))?(?:/([0-9]+))?/?$", Pattern.CASE_INSENSITIVE);

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
                String format = searchMatcher.group(1);
                String type = searchMatcher.group(2);
                String uriTerm = searchMatcher.group(3);
                String pagePart = searchMatcher.group(4);
                String sizePart = searchMatcher.group(5);
                String term = RequestUtils.getSearchString(request, uriTerm);

                if (pagePart != null) {
                    pageIdx = Integer.valueOf(pagePart);
                    if (pageIdx < 1) {
                        throw new ApiRequestException("Page number must be greater than 0");
                    }
                }
                if (sizePart != null) {
                    pageSize = Integer.valueOf(sizePart);
                    if (pageSize > MAX_PAGE_SIZE) {
                        throw new ApiRequestException("Page size must be less than 1000");
                    }
                    else if (pageSize < 1) {
                        throw new ApiRequestException("Page size must be greater than 0");
                    }
                }

                if (!type.equals("search")) {
                    if (type.equals("sponsor")) {
                        term = "sponsor:"+uriTerm+" AND otype:bill";
                        String filter = RequestUtils.getSearchString(request, "");
                        if (!filter.isEmpty()) {
                            term += " AND "+filter;
                        }
                    }
                    else {
                        term = "otype:"+type.substring(0, type.length()-1)+(term.isEmpty() ? "" : " AND "+term);
                    }
                }
                else if (term.isEmpty()) {
                    throw new ApiRequestException("A search term is required.");
                }

                doSearch(request, response, format, type, term, pageIdx, pageSize, sort, sortOrder);
            }
            else if (documentMatcher.find()) {
                String format = documentMatcher.group(1);
                String otype = documentMatcher.group(2);
                String oid = documentMatcher.group(3);
                doSingleView(request, response, format, otype, oid);
            }
            else {
                throw new ApiRequestException("Invalid request: "+uri);
            }
        }
        catch (ApiRequestException e) {
            logger.error(e.getMessage(), e);
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
            int start = (pageNumber-1) * pageSize;
            SenateResponse sr = Application.getLucene().search(term, start, pageSize, sort, sortOrder);
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
            else if (format.equals("rss")) {
                request.setAttribute("term", term);
                request.setAttribute("results", sr);
                request.setAttribute("pageSize", pageSize);
                request.setAttribute("pageIdx", pageNumber);
                response.setContentType("application/rss+xml");
                request.getSession().getServletContext().getRequestDispatcher("/views/search-rss.jsp").forward(request, response);
            }
            else if (format.equals("atom")) {
                request.setAttribute("term", term);
                request.setAttribute("results", sr);
                request.setAttribute("pageSize", pageSize);
                request.setAttribute("pageIdx", pageNumber);
                response.setContentType("application/atom+xml");
                request.getSession().getServletContext().getRequestDispatcher("/views/search-atom.jsp").forward(request, response);
            }
            else if (format.equals("csv")) {
                request.setAttribute("term", term);
                request.setAttribute("results", sr);
                request.setAttribute("pageSize", pageSize);
                request.setAttribute("pageIdx", pageNumber);
                response.setContentType("text/plain");
                request.getSession().getServletContext().getRequestDispatcher("/views/search-csv.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiRequestException("internal server error.");
        }
    }

    private void doSingleView(HttpServletRequest request, HttpServletResponse response, String format, String type, String id) throws ApiRequestException, IOException, ServletException
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
            else if (format.equals("lrs-print")) {
                request.setAttribute("bill", object);
                request.getSession().getServletContext().getRequestDispatcher("/views/bill-lrs-print.jsp").forward(request, response);
            }
            else if (format.equals("pdf")) {
                response.setContentType("application/pdf");
                try {
                    BillTextPDFConverter.write(object, response.getOutputStream());
                } catch (COSVisitorException e) {
                    logger.error(e.getMessage(), e);
                    throw new ApiRequestException("internal server error.", e);
                }
            }
            else if (format.equals("html") || format.equals("html-print")) {
                // TODO: Send a 301 response instead.
                response.sendRedirect(request.getContextPath()+"/"+type+"/"+id );
            }
        }
    }
}
