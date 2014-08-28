package gov.nysenate.openleg.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/report/**")
public class ReportPageCtrl
{
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "report";
    }
}
