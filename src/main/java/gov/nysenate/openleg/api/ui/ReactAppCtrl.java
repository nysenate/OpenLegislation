package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.auth.AuthedUser;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.auth.model.ApiKeyLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
 * Simple entry point to the front-end. Returns the main angular-js driven web page which will handle
 * all the heavy lifting.
 */
@Controller
public class ReactAppCtrl {
    private static final Logger logger = LoggerFactory.getLogger(ReactAppCtrl.class);

    @Autowired
    private Environment environment;

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
            "/manage/**"
    })
    public String home(HttpServletRequest request) {
        String forwardedForIp = request.getHeader("x-forwarded-for");
        String ipAddr = forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        Subject subject = SecurityUtils.getSubject();
        setRequestAttributes(request);
        // Senate staff and API users will be routed to the internal dev interface.
        if (subject.isPermitted("ui:view") || ipAddr.matches(ipWhitelist)) {
            return "index";
        }
        // Non-senate staff and un-authenticated users will see the public page.
        return "publichome";
    }

//    @RequestMapping("/admin/**")
//    public String admin(HttpServletRequest request) {
//        Subject subject = SecurityUtils.getSubject();
//        if (subject.isPermitted("admin:view")) {
//            return home(request);
//        }
//        return "404";
//    }

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

    @RequestMapping("/public")
    public String publicHome() {
        return "publichome";
    }

    @RequestMapping("/subscriptions")
    public String subscriptions() {
        return "subscriptions";
    }

    @ResponseBody
    @RequestMapping("/globals")
    public BaseResponse globals() {
        GlobalsView gv = new GlobalsView(ipWhitelist, environment.getSenSiteUrl(), environment.getOpenlegRefUrl());
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