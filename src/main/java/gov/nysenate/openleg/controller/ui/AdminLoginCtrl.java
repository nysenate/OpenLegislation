package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.service.auth.AdminUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping (value = "/admin/**")
public class AdminLoginCtrl
{
    @Autowired
    private AdminUserService adminService;

    /**
     *
     * @return
     */
    @RequestMapping (value = "", method = RequestMethod.GET)
    public String index (HttpSession session) {
        if (session.getAttribute("user") != null) {
            System.out.println("Session user: " + session.getAttribute("user"));
            return "adminMenu";
        } else
             return "adminlogin";
    }

    /**
     *
     * @param request
     * @param session
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping (value = "", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws ServletException, IOException {
        if (session.getAttribute("user") != null)
        {
            System.out.println("Logging out user: " +session.getAttribute("user") + " from system.");
            session.invalidate();

            return "adminlogin";
        }

        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        System.out.println("Attempted login from user: " +user + ", with password: " + pass);

        int login_code = adminService.login(user, pass);
        System.out.println("Login code: " + login_code);

        switch (login_code)
        {
            case 0: {
                session.setAttribute("user", user);
                return "redirect:/admin";
            }
            default:
            case -1:
            case -2:
            {
                request.setAttribute("errormessage", "Invalid username or password!");
                return "adminlogin";
            }
        }
    }
}
