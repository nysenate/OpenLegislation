package gov.nysenate.openleg.controller.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests for retrieving angular templates.
 */
@Controller
public class PartialTmplCtrl extends BaseContentPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(PartialTmplCtrl.class);

    @PostConstruct
    public void init() {
        baseInit();
    }

    @RequestMapping(value = "/partial/**", method = RequestMethod.GET)
    public String partials(HttpServletRequest request) {
        request.setAttribute("ctxPath", request.getContextPath());
        addContentAttributesToRequest(request);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (path.contains("..")) {
            return "404";
        }
        return path;
    }
}
