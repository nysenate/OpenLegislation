package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.model.auth.AdminUser;
import gov.nysenate.openleg.service.auth.AdminUserService;
import gov.nysenate.openleg.service.auth.InvalidUsernameException;
import gov.nysenate.openleg.service.mail.SendMailService;
import gov.nysenate.openleg.util.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/accounts")
public class AdminAccountCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(AdminAccountCtrl.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private SendMailService sendMailService;

    private static final String registrationEmailSubject = "OpenLegislation admin registration";

    private static final String registrationEmailTemplate =
            "Hello,\n\n" +
            "\tYou are receiving this email because you have been registered as an administrative user of OpenLegislation.  " +
            "Your login credentials are as follows:\n\n\tusername: ${username}\n\tpassword: ${password}";

    private static final int newPassLength = 8;
    private static final int minPassLength = 5;

    /**
     *  Create New Admin User API
     *
     * Registers a new user as an admin.  Sends an email message confirming the registration along with a new,
     *  randomly generated password.  Must be called by a master admin.
     *
     *  (GET) /api/3/admin/accounts/new
     *
     *  Request params: username (string) - The username of the new user, should be an approved email address
     *                  master (boolean) - True if the new user should be a master admin (default false)
     *
     *  Expected Output: successful admin-registered response if the user was created, ErrorResponse otherwise
     */
    @RequiresRoles("masterAdmin")
    @RequestMapping("/create")
    public BaseResponse createNewUser(@RequestParam(required = true) String username,
                                      @RequestParam(defaultValue = "false") boolean master) {

        if (adminUserService.adminInDb(username)) {
            return new ViewObjectErrorResponse(ErrorCode.USER_ALREADY_EXISTS, username);
        }

        String password = RandomUtils.getRandomString(newPassLength);
        try {
            adminUserService.createUser(username, password, true, master);
        } catch (InvalidUsernameException ex) {
            throw new InvalidRequestParamEx(username, "username", "String", ex.getProperFormat());
        }

        sendNewUserEmail(username, password);

        return new SimpleResponse(true, username + " has been successfully registered as an admin user", "admin-registered");
    }

    /**
     *  Remove Admin User API
     *
     *  Deletes the account of an admin user.  Must be called by a master admin.
     *
     *  (GET) /api/3/admin/accounts/remove
     *
     *  Request params: username (string) - The username of the user to be removed
     *
     *  Expected Output: successful admin-deleted if a user was removed, ErrorResponse otherwise
     */
    @RequiresRoles("masterAdmin")
    @RequestMapping("/remove")
    public BaseResponse removeUser(@RequestParam(required = true) String username) {

        if (!adminUserService.adminInDb(username)) {
            return new ViewObjectErrorResponse(ErrorCode.USER_DOES_NOT_EXIST, username);
        }

        adminUserService.deleteUser(username);

        return new SimpleResponse(true, "The admin user " + username + " has been successfully removed", "admin-deleted");
    }

    /**
     *  Change Password API
     *
     *  Changes the password for the calling user.
     *
     *  (POST) /api/3/admin/accounts/passchange
     *
     *  Request params: password (string) - The new password
     *
     *  Expected Output: successful pass-changed response if the password was changed, ErrorResponse otherwise
     */
    @RequiresAuthentication
    @RequestMapping(value = "/passchange", method = RequestMethod.POST)
    public BaseResponse changePassword(@RequestParam(required = true) String password) {

        String username = getSubjectUsername();

        AdminUser user = adminUserService.getAdminUser(username);

        if (BCrypt.checkpw(password, user.getPassword())) {
            return new ErrorResponse(ErrorCode.SAME_PASSWORD);
        }

        if (password.length() < minPassLength) {
            throw new InvalidRequestParamEx(password.replaceAll(".", "*"), "password", "String",
                    "Password must contain at least " + minPassLength + " characters");
        }

        user.setPassword(password);
        adminUserService.createUser(user);

        return new SimpleResponse(true, "Password has been successfully changed", "pass-changed");
    }

    /** --- Internal Methods --- */

    /**
     * @return The username of the current subject
     */
    private String getSubjectUsername() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    /**
     * Sends an email to a new user notifying them of their registration
     * @param username The username/email address of the new user
     * @param password the password of the new user
     */
    @Async
    private void sendNewUserEmail(String username, String password) {
        String message = StrSubstitutor.replace(registrationEmailTemplate,
                                                ImmutableMap.of("username", username, "password", password));
        sendMailService.sendMessage(username, registrationEmailSubject, message);
    }
}
