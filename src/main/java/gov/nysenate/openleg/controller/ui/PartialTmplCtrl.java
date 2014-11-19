package gov.nysenate.openleg.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests for retrieving angular templates.
 */
@Controller
public class PartialTmplCtrl
{
    @RequestMapping(value = "/partial/{type}/{name}", method = RequestMethod.GET)
    public String partials(@PathVariable String type, @PathVariable String name, HttpServletRequest request) {
        request.setAttribute("ctxPath", request.getContextPath());
        return "partial/" + type + "/" + name;
    }
}
