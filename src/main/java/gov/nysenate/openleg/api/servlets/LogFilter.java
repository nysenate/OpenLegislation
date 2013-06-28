package gov.nysenate.openleg.api.servlets;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class LogFilter implements Filter
{
    private final Logger logger = Logger.getLogger(LogFilter.class);
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try {
            String uri = URLDecoder.decode(((HttpServletRequest)request).getRequestURI(), "UTF-8");
            String queryString = ((HttpServletRequest)request).getQueryString();
            if (queryString != null) {
                uri += "?"+queryString;
            }

            logger.info("request: "+uri);
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
        this.filterConfig = null;
    }
}
