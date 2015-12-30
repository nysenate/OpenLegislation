package gov.nysenate.openleg.controller.api.base;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.service.auth.ApiUserService;
import gov.nysenate.openleg.service.log.data.ApiLogDataService;
import gov.nysenate.openleg.service.log.event.ApiLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Intercepts API requests and fires off log events to record the API usage.
 */
@Component ("apiLogFilter")
public class ApiLogFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ApiLogFilter.class);

    @Autowired protected EventBus eventBus;
    @Autowired protected ApiLogDataService logDataService;
    @Autowired protected ApiUserService apiUserService;

    private static String[] IGNORED_PATHS = new String[]{"/api/3/admin/apiLog", "/api/3/admin/process/runs/"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @PostConstruct
    public void postInit() {
        this.eventBus.register(this);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        LocalDateTime requestStart = LocalDateTime.now();
        filterChain.doFilter(servletRequest, servletResponse);
        boolean ignoreApiReq = false;
        for (String path : IGNORED_PATHS) {
            if (((HttpServletRequest) servletRequest).getRequestURI().contains(path)) {
                ignoreApiReq = true;
                break;
            }
        }
        if (!ignoreApiReq) {
            ApiLogEvent apiLogEvent = new ApiLogEvent(servletRequest, servletResponse, requestStart, LocalDateTime.now());
            String apiKey = apiLogEvent.getApiResponse().getBaseRequest().getApiKey();
            Optional<ApiUser> userByKey = apiUserService.getUserByKey(apiKey);
            if (userByKey.isPresent()) {
                apiLogEvent.getApiResponse().getBaseRequest().setApiUser(userByKey.get());
            }
            eventBus.post(apiLogEvent);
        }
    }

    /**
     * The log event is handled here so that the data service can occur asynchronously.
     * @param apiLogEvent ApiLogEvent
     */
    @Subscribe
    public void handleApiLogEvent(ApiLogEvent apiLogEvent) {
        logDataService.saveApiResponseAsync(apiLogEvent, true);
    }

    @Override
    public void destroy() {}
}