package gov.nysenate.openleg.controller.api.base;

import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.service.auth.ApiUserService;
import gov.nysenate.openleg.util.OutputUtils;
import gov.nysenate.openleg.util.UIKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component("apiAuthFilter")
public class ApiAuthFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ApiAuthFilter.class);

    @Autowired
    protected ApiUserService apiUserService;

    @Value("${api.secret}") private String apiSecret;
    @Value("${api.auth.ip.whitelist}") private String filterAddress;
    @Value("${api.auth.enable}") private boolean enabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String key = servletRequest.getParameter("key");
        String ipAddress = servletRequest.getLocalAddr();

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (enabled) {
            if (ipAddress.matches(filterAddress) || apiUserService.validateKey(key)) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            else {
                ErrorResponse errorResponse = new ErrorResponse(ErrorCode.API_KEY_REQUIRED);
                response.getWriter().append(OutputUtils.toJson(errorResponse));
                response.setContentType("application/json");
                response.setStatus(401);
                response.flushBuffer();
                logger.info("Invalid key used in API request.");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }
}