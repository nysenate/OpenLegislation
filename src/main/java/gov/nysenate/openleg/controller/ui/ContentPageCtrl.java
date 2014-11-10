package gov.nysenate.openleg.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ContentPageCtrl
{
    @RequestMapping(value = "/bills", method = RequestMethod.GET)
    public String bills() {
        return "bills";
    }

    @RequestMapping(value = "/calendars", method = RequestMethod.GET)
    public String calendars() {
        return "calendars";
    }
}
