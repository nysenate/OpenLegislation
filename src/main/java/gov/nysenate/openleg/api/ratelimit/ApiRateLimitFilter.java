package gov.nysenate.openleg.api.ratelimit;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimitSettings.Mode;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Admitted;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Limit;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Slot;
import gov.nysenate.openleg.api.ratelimit.ApiRateLimiter.Rejected;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.auth.service.ApiUserLoginAuthRealm;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.common.util.OutputUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Limits the {@code /api/3} requests made with each API key: how fast they arrive and how many are
 * in flight at once. A request over a limit is rejected with a 429 before any controller sees it,
 * or, in observe mode, only counted.
 *
 * <p>The key is the one the request is logged in with, by its {@code key} parameter or a
 * {@code /loginapikey} session, so both share one budget. This filter must run after
 * {@code ApiAuthFilter} has logged the key in. Requests without a key, such as from the IP
 * whitelist or an admin session, OPTIONS requests, admin API requests and keys with the
 * {@link OpenLegRole#API_RATE_LIMIT_EXEMPT} role are never limited.
 *
 * <p>Each request is decided once, even when it is forwarded, and an admitted request holds its
 * slot until the rest of the chain returns, however it ends.
 */
@Component("apiRateLimitFilter")
public class ApiRateLimitFilter extends OncePerRequestFilter {
    /** Requests per second are a whole number, so the next token is never more than a second away. */
    private static final String RETRY_AFTER_SECONDS = "1";

    private final Mode mode;
    private final ApiRateLimits limits;
    /** Null when the mode is off, which never reaches {@link #doFilterInternal}. */
    private final ApiRateLimiter limiter;
    private final ApiRateLimitTelemetry telemetry;
    private final ApiUserLoginAuthRealm apiKeyRealm;
    private final ApiUserService apiUserService;

    @Autowired
    public ApiRateLimitFilter(ApiRateLimitSettings settings, Ticker apiRateLimitTicker,
                              ApiRateLimitTelemetry telemetry, ApiUserLoginAuthRealm apiKeyRealm,
                              ApiUserService apiUserService) {
        this.mode = settings.getMode();
        this.limits = settings.getLimits();
        this.limiter = mode == Mode.OFF ? null : new ApiRateLimiter(limits, apiRateLimitTicker);
        this.telemetry = telemetry;
        this.apiKeyRealm = apiKeyRealm;
        this.apiUserService = apiUserService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return mode == Mode.OFF || HttpMethod.OPTIONS.matches(request.getMethod()) || isAdminApi(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<ApiUser> apiUser = apiKeyRealm.getApiKey(SecurityUtils.getSubject())
                .flatMap(apiUserService::getUserByKey)
                .filter(ApiUser::isAuthenticated);
        // A session can outlive its key. Such a request is left alone, like one without a key,
        // so only valid keys get a budget.
        if (apiUser.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        String apiKey = apiUser.get().getApiKey();
        // Check the current cached user roles rather than roles saved in the session.
        // Role changes apply to subsequent requests once the user cache is updated.
        ImmutableSet<OpenLegRole> roles = apiUser.get().getGrantedRoles();
        if (roles.contains(OpenLegRole.API_RATE_LIMIT_EXEMPT)) {
            telemetry.recordExempt();
            chain.doFilter(request, response);
            return;
        }
        boolean senateSite = roles.contains(OpenLegRole.SEN_SITE_API_USER);
        switch (limiter.tryAdmit(apiKey)) {
            case Admitted admitted -> {
                try (Slot ignored = admitted.slot()) {
                    telemetry.recordAdmitted(apiKey, senateSite, admitted.inFlight());
                    chain.doFilter(request, response);
                }
            }
            case Rejected rejected -> {
                telemetry.recordRejected(apiKey, senateSite, rejected.limit(), rejected.inFlight());
                if (mode == Mode.ENFORCE) {
                    writeRejection(response, rejected.limit());
                } else {
                    chain.doFilter(request, response);
                }
            }
        }
    }

    /** Matches admin API paths as Shiro does when it requires an admin login for them. */
    private static boolean isAdminApi(HttpServletRequest request) {
        String path = WebUtils.getPathWithinApplication(request);
        return path.equals(BaseCtrl.BASE_ADMIN_API_PATH) || path.startsWith(BaseCtrl.BASE_ADMIN_API_PATH + "/");
    }

    private void writeRejection(HttpServletResponse response, Limit limit) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.API_RATE_LIMIT_EXCEEDED);
        errorResponse.setMessage(switch (limit) {
            case RATE -> "Too many requests: this API key is limited to %d requests per second. Wait and try again."
                    .formatted(limits.requestsPerSecond());
            case CONCURRENCY -> ("Too many requests at once: this API key is limited to %d requests in progress " +
                    "at the same time. Wait for them to finish and try again.").formatted(limits.maxConcurrent());
        });
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, RETRY_AFTER_SECONDS);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(OutputUtils.toJson(errorResponse));
    }
}
