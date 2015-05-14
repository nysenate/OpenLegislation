package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.util.RandomUtils;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

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
        // A UI key is set in order to allow front end api calls without an API key
        addUIKeyParams(request);
        // Render the main angular app
        return "home";
    }

    private void addUIKeyParams(HttpServletRequest request) {
        String randomUiKey = RandomUtils.getRandomString(64);
        request.setAttribute("gaTrackingId", gaTrackingId);
        request.getSession().setMaxInactiveInterval(3600);
        request.getSession().setAttribute("uiKey", randomUiKey);
        request.setAttribute("uiKey", randomUiKey);
    }

    @RequestMapping("/admin")
    public String adminLogin() {
        return "redirect:/";
    }
}