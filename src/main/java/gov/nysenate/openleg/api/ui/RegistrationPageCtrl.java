package gov.nysenate.openleg.api.ui;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.auth.NewUserView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.auth.exception.UsernameExistsException;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/register")
public class RegistrationPageCtrl extends BaseCtrl {
    private final ApiUserService apiUserService;

    @Autowired
    public RegistrationPageCtrl(ApiUserService apiUserService) {
        this.apiUserService = apiUserService;
    }

    private static final Logger logger = LoggerFactory.getLogger(RegistrationPageCtrl.class);

    /**
     * Activate a user's account with their provided registration token.
     *
     * @param regToken The user's registration token
     * @return The index to return them to
     */
    @RequestMapping(value = "/token/{regToken}", method = RequestMethod.GET)
    public BaseResponse register(@PathVariable String regToken) {
        try {
            apiUserService.activateUser(regToken);
        } catch (Exception e) {
            logger.error("There was an issue with activating a user's reg token!", e);
            return new SimpleResponse(false, "Invalid token", "Email confirmation");
        }
        return new SimpleResponse(true, "Successfully confirmed email address.", "Email confirmation");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public BaseResponse signup(@RequestBody NewUserView body) {
        String email = body.getEmail();
        String name = body.getName();
        Set<String> subscriptions = body.getSubscriptions();
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        for (String sub : subscriptions) {
            subs.add(getEnumParameter("Subscriptions", sub, ApiUserSubscriptionType.class));
        }
        logger.info("{} with email {} is registering for an API key.", name, email);
        if (StringUtils.isBlank(email)) {
            return new SimpleResponse(false, "Email must be valid.", "api-signup");
        }
        if (StringUtils.isBlank(name)) {
            return new SimpleResponse(false, "Name must not be empty.", "api-signup");
        }
        try {
            ApiUser apiUser = apiUserService.registerNewUser(email, name, "", subs);
            return new SimpleResponse(true, apiUser.getName() + " has been registered.", "api-signup");
        } catch (UsernameExistsException | IllegalArgumentException ex) {
            return new SimpleResponse(false, ex.getMessage(), "api-signup");
        } catch (MailAuthenticationException ex) {
            logger.error(ex.getMessage(), ex);
            return new SimpleResponse(false, "Error sending confirmation email.", "api-signup");
        }
    }
}
