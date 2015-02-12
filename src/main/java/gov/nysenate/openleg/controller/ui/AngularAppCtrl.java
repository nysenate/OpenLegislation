package gov.nysenate.openleg.controller.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple entry point to the front-end. Returns the main angular-js driven web page which will handle
 * all the heavy lifting.
 */
@Controller
public class AngularAppCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AngularAppCtrl.class);

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
    public String home() {
        return "home";
    }

    @RequestMapping("/admin")
    public String adminLogin() {
        return "redirect:/";
    }
}
