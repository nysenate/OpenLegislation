package gov.nysenate.openleg.api.auth;

import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.auth.model.ApiKeyLoginToken;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("apiAuthFilter")
public class ApiAuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiAuthFilter.class);

    private final ApiUserService apiUserService;
    @Value("${api.auth.ip.whitelist}")
    private String filterAddress;
    @Value("${api.auth.enable}")
    private boolean enabled;

    @Autowired
    public ApiAuthFilter(ApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        String key = request.getParameter("key");
        String forwardedForIp = ((HttpServletRequest) request).getHeader("x-forwarded-for");
        String ipAddress = forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;

        Subject subject = SecurityUtils.getSubject();
        if (!enabled || authenticate(subject, ipAddress, key)) {
            filterChain.doFilter(request, response);
        } else {
            logger.warn("Invalid key used in API request. ip: [{}] key: [{}]", ipAddress, key);
            if (subject.isRemembered()) {
                // We should log out of the current session for security purposes.
                subject.logout();
            }
            writeErrorResponse((HttpServletResponse) response);
        }
    }

    @Override
    public void destroy() {}

    /**
     * Authenticate the subject using one of two authentication methods
     *  - API key authentication if a key is provided
     *  - test for ip whitelist match or existing session from ui login
     * @param subject Subject
     * @param ipAddress String
     * @param key String
     * @return boolean - true iff user was successfully authenticated
     */
    private boolean authenticate(Subject subject, String ipAddress, String key) {
        // Authenticate based on a key, if one is provided
        if (!StringUtils.isEmpty(key)) {
            return authenticateKey(subject, ipAddress, key);
        }
        // Grant access if user is in ip whitelist, or authenticated via the ui
        return !StringUtils.isEmpty(ipAddress) && ipAddress.matches(filterAddress) ||
                subject.isPermitted("ui:view");

    }

    /**
     * Authenticate the subject using the given api key
     * @param subject Subject
     * @param ipAddress String
     * @param key String
     * @return boolean - true iff the passed in key is valid
     */
    private boolean authenticateKey(Subject subject, String ipAddress, String key) {
        // Return true if the user is already authenticated using the same key
        if (key.equals(subject.getPrincipal())) {
            return true;
        }
        // Validate the key and login if it is valid
        if (apiUserService.validateKey(key)) {
            subject.login(new ApiKeyLoginToken(key, ipAddress));
            return true;
        }
        return false;
    }

    /**
     * Write an error json response
     * @param response HttpServletResponse
     * @throws IOException if it failed to write or flush buffer.
     */
    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.API_KEY_REQUIRED);
        response.getWriter().append(OutputUtils.toJson(errorResponse));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }
}