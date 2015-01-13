package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.config.Environment;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class ContentPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(ContentPageCtrl.class);

    @Autowired Environment env;

    @RequestMapping(value = {"", "bills/**", "agendas/**", "calendars/**", "laws/**", "transcripts/**"},
                    method = RequestMethod.GET)
    public String content(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        return "content";
    }
}
