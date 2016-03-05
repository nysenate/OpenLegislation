package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.model.auth.ApiKeyLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Simple entry point to the front-end. Returns the main angular-js driven web page which will handle
 * all the heavy lifting.
 */
@Controller
public class AngularAppCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AngularAppCtrl.class);

    @Value("${ga.tracking.id}") private String gaTrackingId;
    @Value("${api.auth.ip.whitelist}") private String ipWhitelist;

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
        String ipAddr= forwardedForIp == null ? request.getRemoteAddr() : forwardedForIp;
        // Google Analytics
        request.setAttribute("gaTrackingId", gaTrackingId);
        Subject subject = SecurityUtils.getSubject();
        // Senate staff and API users will be routed to the internal dev interface.
        if (subject.isPermitted("ui:view") || ipAddr.matches(ipWhitelist)) {
            return "home";
        }
        // Non-senate staff and un-authenticated users will see the public page.
        return "publichome";
    }

    @RequestMapping("/admin/**")
    public String admin(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isPermitted("admin:view")) {
            return "home";
        }
        return "404";
    }

    @ResponseBody
    @RequestMapping(value = "/loginapikey", method = RequestMethod.POST)
    public BaseResponse login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        String apiKey = body.get("apiKey");
        try {
            SecurityUtils.getSubject().login(new ApiKeyLoginToken(apiKey, ipAddr));
            return new SimpleResponse(true, "Login successful", "apikey-login");
        }
        catch (AuthenticationException ex) {
            logger.info("Invalid API Key attempt with key: {}", apiKey);
        }
        return new ErrorResponse(ErrorCode.API_KEY_INVALID);
    }

    @RequestMapping("/public")
    public String publicHome() {
        return "publichome";
    }
}