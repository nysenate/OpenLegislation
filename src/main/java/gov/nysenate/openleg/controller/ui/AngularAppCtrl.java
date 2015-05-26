package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.util.UIKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple entry point to the front-end. Returns the main angular-js driven web page which will handle
 * all the heavy lifting.
 */
@Controller
public class AngularAppCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AngularAppCtrl.class);

    @Value("${ga.tracking.id}") private String gaTrackingId;
    @Value("${api.secret}") private String apiSecret;

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
                     "/admin/**"})
    public String home(HttpServletRequest request) {
        // Google Analytics
        request.setAttribute("gaTrackingId", gaTrackingId);
        // A UI key is set in order to allow front end api calls without an API key
        UIKeyUtil.setUIKey(request, apiSecret, "uiKey");
        // Render the main angular app
        return "home";
    }
}