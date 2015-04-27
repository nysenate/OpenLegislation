package gov.nysenate.openleg.controller.api.base;

import gov.nysenate.openleg.dao.auth.ApiLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component ("apiLogFilter")
public class ApiLogFilter implements Filter
{
    @Autowired
    protected ApiLogDao apiLogDao;

    private static final Logger logger = LoggerFactory.getLogger(ApiLogFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
