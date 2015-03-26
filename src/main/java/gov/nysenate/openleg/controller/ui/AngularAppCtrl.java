package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.util.RandomUtils;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

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
                     "/manage/**"})
    public String home(HttpServletRequest request) {
        request.setAttribute("gaTrackingId", gaTrackingId);
        String randomUiKey = RandomUtils.getRandomString(64);
        request.getSession().setAttribute("uiKey", randomUiKey);
        request.setAttribute("uiKey", randomUiKey);
        return "home";
    }

    @RequestMapping("/admin")
    public String adminLogin() {
        return "redirect:/";
    }
}
