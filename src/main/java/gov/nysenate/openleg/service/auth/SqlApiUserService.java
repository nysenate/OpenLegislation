package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.service.mail.MimeSendMailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class SqlApiUserService implements ApiUserService
{
    @Autowired
    protected ApiUserDao apiUserDao;

    @Autowired
    protected MimeSendMailService sendMailService;


    @Value("${domain.url}") private String domainUrl;

    private static final Logger logger = LoggerFactory.getLogger(SqlApiUserService.class);

    /**
     * This method will be called whenever there is an attempt to register a new user.
     * The appropriate checks will be made to ensure that a registration will only be successful if the
     * given email address has not already been sued for registration
     * @param email The user's submitted email address
     * @param name The entered name
     * @param orgName The entered name of the user's organization
     * @return A new ApiUser object if the registration is successful
     */
    @Override
    public ApiUser registerNewUser(String email, String name, String orgName) {
        Pattern emailRegex = Pattern.compile("^[a-zA-Z\\d-._]+@[a-zA-Z\\d-._]+.[a-zA-Z]{2,4}$");
        Matcher patternMatcher = emailRegex.matcher(email);

        if (!patternMatcher.find())
            throw new IllegalArgumentException("Invalid email address format used used in registration attempt!");

        try {
            if (apiUserDao.getApiUserFromEmail(email) != null)
                throw new IllegalArgumentException("Email address already in use!");

        } catch (EmptyResultDataAccessException e) {

        }

        ApiUser newUser = new ApiUser(email);
        newUser.setName(name);
        newUser.setOrganizationName(orgName);
        newUser.setAuthStatus(false);
        newUser.setRegistrationToken(RandomStringUtils.randomAlphanumeric(32));
        newUser.setActive(true);

        apiUserDao.insertUser(newUser);
        sendRegistrationEmail(newUser);
        return newUser;
    }

    /**
     * Get an API User from a given email address
     * @param email The email address of the user being search for.
     * @return An APIUser if the email is valid
     */
    @Override
    public ApiUser getUser (String email) {
        return apiUserDao.getApiUserFromEmail(email);
    }

    /**
     * Attempt to activate a user based on the provided registration token. If a valid registration
     * token is indeed supplied, then that user will have their account activated, and their
     * API Key will be sent to them via email.
     *
     * @param registrationToken The supplied registration token.
     */
    @Override
    public void activateUser(String registrationToken) {
        try {
            ApiUser user = apiUserDao.getApiUserFromToken(registrationToken);
            user.setActive(true);
            user.setAuthStatus(true);
            apiUserDao.updateUser(user);
            sendApikeyEmail(user);

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Invalid registration token supplied!");
        }
    }

    /**
     * This method will send a user a confirmation email, containing a link holding their registration token,
     * which will allow them to activate their account.
     *
     * @param user The user to send the registration information to
     */
    public void sendRegistrationEmail(ApiUser user) {
        String message = String.format("Hello %s,\n\n\tThank you for your interest in Open Legislation. " +
                  "In order to receive your API key you must first activate your account by visiting the link below. " +
                  "Once you have confirmed your email address, an email will be sent to you containing your API Key.\n\n" +
                  "Activate your account here:\n%s/%s" +
                  "\n\n-- NY Senate Development Team", user.getName(), domainUrl + "/register", user.getRegistrationToken());

         sendMailService.sendMessage(user.getEmail(), "Open Legislation API Account Registration", message);
    }

    /**
     * This method will send a user an email containing their API Key.
     * It is called whenever a user confirms their email address via their registration token.
     * @param user The user to send the API Key to.
     */
    public void sendApikeyEmail(ApiUser user) {
        String message = String.format("Hello %s,\n\n\tThank you for your interest in Open Legislation." +
                "Your API Key is the following:\n%s\n\n-- NY Senate Development Team", user.getName(), user.getApikey());

        sendMailService.sendMessage(user.getEmail(), "Your Open Legislation API Key", message);
    }
}
