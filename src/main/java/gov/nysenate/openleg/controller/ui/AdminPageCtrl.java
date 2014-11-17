package gov.nysenate.openleg.controller.ui;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/**")
public class AdminPageCtrl
{
    @RequiresAuthentication
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin";
    }
}
