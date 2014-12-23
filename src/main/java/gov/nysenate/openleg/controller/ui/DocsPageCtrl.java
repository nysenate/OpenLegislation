package gov.nysenate.openleg.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DocsPageCtrl
{
    @RequestMapping("docs")
    public String bleh() {
        return  "redirect:/static/docs/html";
    }
}
