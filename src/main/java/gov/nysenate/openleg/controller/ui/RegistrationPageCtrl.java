package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.service.auth.ApiUserService;
import gov.nysenate.openleg.service.auth.UsernameExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/register")
public class RegistrationPageCtrl
{
    @Autowired
    protected ApiUserService apiUserService;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationPageCtrl.class);

    /**
     * Activate a user's account with their provided registration token.
     * @param regToken The user's registration token
     * @return The index to return them to
     */
    @RequestMapping(value = "/token/{regToken}", method = RequestMethod.GET)
    public String index(@PathVariable String regToken) {
        logger.info("Token: " +regToken);
        try {
            apiUserService.activateUser(regToken);
        } catch (Exception e) {
        }
        return "register";
    }

    @ResponseBody
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public BaseResponse signup(WebRequest webRequest) {
        String email =  webRequest.getParameter("email");
        String name =  webRequest.getParameter("name");
        try {
            ApiUser apiUser = apiUserService.registerNewUser(email, name, "");
            return new SimpleResponse(true, apiUser.getName() + " has been registered.", "api-signup");
        }
        catch (UsernameExistsException ex) {
            return new SimpleResponse(false, ex.getMessage(), "api-signup");
        }
    }
}