package gov.nysenate.openleg.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/about/**")
public class AboutPageCtrl
{
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpSession session) {
        return "about";
    }
}
