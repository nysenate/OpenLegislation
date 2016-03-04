package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.entity.AdminUserView;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.model.auth.AdminUser;
import gov.nysenate.openleg.service.auth.AdminUserService;
import gov.nysenate.openleg.service.auth.InvalidUsernameException;
import gov.nysenate.openleg.service.auth.OpenLegRole;
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
import org.apache.shiro.authz.annotation.RequiresUser;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/accounts")
public class AdminAccountCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AdminAccountCtrl.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    Environment environment;

    private static final String registrationEmailSubject = "OpenLegislation admin registration";

    private static final String registrationEmailTemplate =
            "Hello,\n\n" +
            "\tYou are receiving this email because you have been registered as an administrative user of OpenLegislation.  " +
            "Your login credentials are as follows:\n\n\tusername: ${username}\n\tpassword: ${password}\n\n" +
            "Log in at ${base_url}/admin/account to access your account";

    private static final int newPassLength = 8;
    private static final int minPassLength = 5;

    @RequiresUser
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public BaseResponse logout() {
        SecurityUtils.getSubject().logout();
        return new SimpleResponse(true, "you have been logged out", "logout");
    }

    @RequiresPermissions("admin:account:view")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getAdminUsers() {
        return new ViewObjectResponse<>(ListView.of(
                adminUserService.getAdminUsers().stream()
                        .map(AdminUserView::new)
                        .collect(Collectors.toList())));
    }

    @RequiresPermissions("admin:account:view")
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET)
    public BaseResponse getAdminUser(@PathVariable String username) {
        if (StringUtils.isBlank(username)) {
            return getAdminUsers();
        }
        if (!adminUserService.adminInDb(username)) {
            throw new UserNotFoundException(username);
        }
        return new ViewObjectResponse<>(new AdminUserView(adminUserService.getAdminUser(username)));
    }

    /**
     * Create New Admin User API
     * -------------------------
     *
     *  Registers a new user as an admin.  Sends an email message confirming the registration along with a new,
     *  randomly generated password.  Must be called by a master admin.
     *
     *  (GET) /api/3/admin/accounts/new
     *
     *  Request params: username (string) - The username of the new user, should be an approved email address
     *                  master (boolean) - True if the new user should be a master admin (default false)
     *
     *  Expected Output: successful admin-registered response if the user was created, ErrorResponse otherwise
     */
    @RequiresPermissions("admin:account:modify")
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.POST)
    public Object createNewUser(@PathVariable String username,
                                @RequestParam(defaultValue = "false") boolean master) {

        if (adminUserService.adminInDb(username)) {
            return new ResponseEntity<>(
                    new ViewObjectErrorResponse(ErrorCode.USER_ALREADY_EXISTS, username), HttpStatus.CONFLICT);
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
     *  ---------------------
     *
     *  Deletes the account of an admin user.  Must be called by a master admin.
     *
     *  (GET) /api/3/admin/accounts/remove
     *
     *  Request params: username (string) - The username of the user to be removed
     *
     *  Expected Output: successful admin-deleted if a user was removed, ErrorResponse otherwise
     */
    @RequiresPermissions("admin:account:modify")
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.DELETE)
    public Object removeUser(@PathVariable String username) {
        if (!adminUserService.adminInDb(username)) {
            throw new UserNotFoundException(username);
        }

        if (environment.getDefaultAdminName().equals(username)) {
            return new ResponseEntity<>(
                    new ViewObjectErrorResponse(ErrorCode.CANNOT_DELETE_ADMIN, username), HttpStatus.FORBIDDEN);
        }

        adminUserService.deleteUser(username);

        return new SimpleResponse(true, "The admin user " + username + " has been successfully removed", "admin-deleted");
    }

    /**
     *  Change Password API
     *  -------------------
     *
     *  Changes the password for the calling user.
     *
     *  (POST) /api/3/admin/accounts/passchange
     *
     *  Request params: password (string) - The new password
     *
     *  Expected Output: successful pass-changed response if the password was changed, ErrorResponse otherwise
     */
    @RequiresPermissions("admin")
    @RequestMapping(value = "/passchange", method = RequestMethod.POST)
    public Object changePassword(@RequestParam(required = true) String password) {
        String username = getSubjectUsername();
        AdminUser user = adminUserService.getAdminUser(username);
        if (BCrypt.checkpw(password, user.getPassword())) {
            return new ResponseEntity<>(
                    new ErrorResponse(ErrorCode.SAME_PASSWORD), HttpStatus.BAD_REQUEST);
        }
        if (password.length() < minPassLength) {
            throw new InvalidRequestParamEx(password.replaceAll(".", "*"), "password", "String",
                    "Password must contain at least " + minPassLength + " characters");
        }
        user.setPassword(password);
        adminUserService.createUser(user);
        return new SimpleResponse(true, "Password has been successfully changed", "pass-changed");
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.USER_DOES_NOT_EXIST, ex.getUsername());
    }

    /**
     * --- Internal Methods ---
     */

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
                ImmutableMap.of("username", username, "password", password, "base_url", environment.getUrl()));
        sendMailService.sendMessage(username, registrationEmailSubject, message);
    }

    private class UserException extends RuntimeException
    {
        private static final long serialVersionUID = -6422565299854256546L;
        private String username;

        public UserException(String message, String username) {
            super(message);
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

    private class UserNotFoundException extends UserException {
        private static final long serialVersionUID = 3276041543957882445L;
        public UserNotFoundException(String username) {
            super("User " + username + " was not found!", username);
        }
    }
}
