package gov.nysenate.openleg.controller.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = "/logout/**")
public class LogoutCtrl
{

    private static final Logger logger = LoggerFactory.getLogger(LogoutCtrl.class);

    /**
     * Log out the currently active admin and invalidate their session.
     * @param session The active session
     * @return A redirection to the bills page.
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String logout(HttpSession session)  throws ServletException, IOException {
        if (session.getAttribute("user") == null)
            return "redirect:/admin";
        else {
            session.invalidate();
            logger.info("Logout Complete");
            return "redirect:/logout";
        }
    }
}