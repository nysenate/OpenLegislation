package gov.nysenate.openleg.controller.api.base;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.log.ApiLogDao;
import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component ("apiLogFilter")
public class ApiLogFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ApiLogFilter.class);

    @Autowired protected EventBus eventBus;
    @Autowired protected ApiLogDao apiLogDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @PostConstruct
    public void postInit() {
        this.eventBus.register(this);
    }

    private class ApiLogEvent {

        public ServletRequest servletRequest;
        public ServletResponse servletResponse;
        public LocalDateTime requestStart;
        public LocalDateTime requestEnd;

        public ApiLogEvent(ServletRequest servletRequest, ServletResponse servletResponse, LocalDateTime requestStart,
                           LocalDateTime requestEnd) {
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
            this.requestStart = requestStart;
            this.requestEnd = requestEnd;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        LocalDateTime requestStart = LocalDateTime.now();
        filterChain.doFilter(servletRequest, servletResponse);
        LocalDateTime requestEnd = LocalDateTime.now();
        eventBus.post(new ApiLogEvent(servletRequest, servletResponse, requestStart, requestEnd));
    }

    @Subscribe
    public void handleApiLogEvent(ApiLogEvent apiLogEvent) {
        logAsyncRequestResponse(apiLogEvent);
    }

    @Async
    public void logAsyncRequestResponse(ApiLogEvent apiLogEvent) {
        try {
            ApiRequest apiRequest = new ApiRequest((HttpServletRequest) apiLogEvent.servletRequest, apiLogEvent.requestStart);
            apiRequest.setRequestId(apiLogDao.saveApiRequest(apiRequest));
            ApiResponse apiResponse = new ApiResponse(apiRequest, (HttpServletResponse) apiLogEvent.servletResponse,
                    apiLogEvent.requestEnd);
            apiLogDao.saveApiResponse(apiResponse);
        }
        catch (DataAccessException ex) {
            logger.error("Error while saving api req/res log.", ex);
        }
    }

    @Override
    public void destroy() {}
}
