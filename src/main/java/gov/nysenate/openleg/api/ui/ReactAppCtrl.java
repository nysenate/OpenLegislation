package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.auth.AuthedUser;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.auth.model.ApiKeyLoginToken;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.bill.BillId;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Simple entry point to the front-end. Returns the main react app which will handle
 * all the heavy lifting.
 */
@Controller
public class ReactAppCtrl {
    private static final Logger logger = LoggerFactory.getLogger(ReactAppCtrl.class);

    @Autowired
    private OpenLegEnvironment environment;

    @Value("${ga.tracking.id}")
    private String gaTrackingId;
    @Value("${api.auth.ip.whitelist}")
    private String ipWhitelist;

    @RequestMapping({"/",
            "/data/**",
            "/bills/**",
            "/calendars/**",
            "/agendas/**",
            "/transcripts/**",
            "/members/**",
            "/laws/**",
            "/sources/**",
            "/reports/**",
            "/manage/**",
            "/admin/**",
            "/register/**",
            "/subscriptions/**",
            "/public",
    })
    public String home(HttpServletRequest request) {
        setRequestAttributes(request);
        return "forward:/static/dist/index.html";
    }

    /**
     * Redirects amendment-suffixed bill URLs (e.g. /bills/2024/S1234A) to the
     * base print number with the amendment as a query param (/bills/2024/S1234?amendment=A).
     * This matches the public website's handling of amendments.
     */
    @GetMapping(value = "/bills/{sessionYear:\\d{4}}/{printNo:[A-Z]\\d+[A-Z]}")
    public String redirectAmendedBill(@PathVariable int sessionYear,
                                      @PathVariable String printNo,
                                      HttpServletRequest request) {
        var billId = new BillId(printNo, sessionYear);
        String existing = request.getQueryString();
        String prefix = (existing == null || existing.isEmpty()) ? "" : existing + "&";
        return "redirect:/bills/%d/%s?%samendment=%s".formatted(sessionYear, billId.getBasePrintNo(), prefix, billId.getVersion().toString());
    }

    @ResponseBody
    @RequestMapping(value = "/loginapikey", method = RequestMethod.POST)
    public BaseResponse login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        String apiKey = body.get("apiKey");
        try {
            SecurityUtils.getSubject().login(new ApiKeyLoginToken(apiKey, ipAddr));
            return new ViewObjectResponse<>(new AuthedUser(true, false));
        } catch (AuthenticationException ex) {
            logger.info("Invalid API Key attempt with key: {}", apiKey);
        }
        return new ErrorResponse(ErrorCode.API_KEY_INVALID);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public BaseResponse loginAdmin(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        String host = request.getRemoteAddr();
        try {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password, host));
            return new ViewObjectResponse<>(new AuthedUser(true, true));
        } catch (AuthenticationException ex) {
            return new ErrorResponse(ErrorCode.UNAUTHORIZED);
        }
    }

    @ResponseBody
    @RequestMapping("/globals")
    public BaseResponse globals(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr = forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        GlobalsView gv = new GlobalsView(ipWhitelist, ipAddr,
                environment.getSenSiteUrl(), environment.getOpenlegRefUrl());
        return new ViewObjectResponse<>(gv);
    }

    /* --- Internal Methods --- */

    private void setRequestAttributes(ServletRequest request) {
        // Google Analytics
        request.setAttribute("gaTrackingId", gaTrackingId);
        // NYSenate.gov url
        request.setAttribute("senSitePath", environment.getSenSiteUrl());
        // Openleg reference URL
        request.setAttribute("openlegRefPath", environment.getOpenlegRefUrl());
    }
}