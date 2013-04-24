package gov.nysenate.openleg.api.servlets;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class LogFilter implements Filter
{
    private final Logger logger = Logger.getLogger(LogFilter.class);
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        }
        catch (IOException e) {
            logger.error("Uncaught exception",e);
            throw e;
        }
        catch (ServletException e) {
            logger.error("Uncaught exception",e);
            throw e;
        }
    }


    public void destroy() {
        this.filterConfig = null;
    }
}
