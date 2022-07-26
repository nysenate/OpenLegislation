package gov.nysenate.openleg.auth.user;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.logs.ApiLogEvent;
import gov.nysenate.openleg.auth.model.ApiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Intercepts API requests and fires off log events to record the API usage.
 */
@Component ("apiLogFilter")
public class ApiLogFilter implements Filter {
    private static final String ignoredPaths = BaseCtrl.BASE_ADMIN_API_PATH + "/(apiLog|runs).*";
    private final EventBus eventBus;
    private final ApiUserService apiUserService;

    @Autowired
    public ApiLogFilter(EventBus eventBus, ApiUserService apiUserService) {
        this.eventBus = eventBus;
        this.apiUserService = apiUserService;
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        LocalDateTime requestStart = LocalDateTime.now();
        filterChain.doFilter(servletRequest, servletResponse);
        if (((HttpServletRequest) servletRequest).getRequestURI().matches(ignoredPaths)) {
            return;
        }
        ApiLogEvent apiLogEvent = new ApiLogEvent(servletRequest, servletResponse, requestStart, LocalDateTime.now());
        String apiKey = apiLogEvent.getApiResponse().getBaseRequest().getApiKey();
        Optional<ApiUser> userByKey = apiUserService.getUserByKey(apiKey);
        userByKey.ifPresent(apiUser -> apiLogEvent.getApiResponse().getBaseRequest().setApiUser(apiUser));
        eventBus.post(apiLogEvent);
    }

    @Override
    public void destroy() {}
}