package gov.nysenate.openleg.controller.ui;

import gov.nysenate.openleg.service.auth.ApiUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/register/")
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
    @RequestMapping(value = "/{regToken}", method = RequestMethod.GET)
    public String index(@PathVariable String regToken) {
        logger.info("Token: " +regToken);
        try {
            apiUserService.activateUser(regToken);
        } catch (Exception e) {
        }
        return "home";
    }
}