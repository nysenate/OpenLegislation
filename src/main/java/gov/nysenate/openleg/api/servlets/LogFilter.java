package gov.nysenate.openleg.api.servlets;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LogFilter implements Filter
{
    private final Logger logger = Logger.getLogger(LogFilter.class);

    public void init(FilterConfig filterConfig)
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try {
            String uri = ((HttpServletRequest)request).getServletPath();
            String pathInfo = ((HttpServletRequest)request).getPathInfo();
            String queryString = ((HttpServletRequest)request).getQueryString();

            if (pathInfo != null) {
                uri += pathInfo;
            }

            if (queryString != null) {
                uri += "?"+queryString;
            }

            if (!uri.contains("/static/")) {
                logger.info("request: "+uri);
            }

            chain.doFilter(request, response);
        }
        catch (IOException e) {
            logger.fatal("Uncaught exception",e);
            throw e;
        }
        catch (ServletException e) {
            logger.fatal("Uncaught exception",e);
            throw e;
        }
    }


    public void destroy()
    {

    }
}
