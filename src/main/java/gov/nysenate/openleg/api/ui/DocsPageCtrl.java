package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class DocsPageCtrl {
    @RequestMapping("docs")
    public BaseResponse docs(WebRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(request.getContextPath() + "/static/docs/html/index.html");
            return null;
        }
        catch (IOException ex) {
            return new ErrorResponse(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
