package gov.nysenate.openleg.api.admin;

import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.auth.AdminUserView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.auth.admin.AdminUser;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.exception.InvalidUsernameException;
import gov.nysenate.openleg.common.util.RandomUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.mail.SendMailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.Serial;
import java.util.Map;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/accounts")
public class AdminAccountCtrl extends BaseCtrl {
    private static final String registrationEmailSubject = "OpenLegislation admin registration";
    private static final String registrationEmailTemplate =
            """
                    Hello,

                    \tYou are receiving this email because you have been registered as an administrative user of OpenLegislation. Your login credentials are as follows:

                    \tusername: ${username}
                    \tpassword: ${password}

                    Log in at ${base_url}/admin/account to access your account""";

    private static final int newPassLength = 8;
    private static final int minPassLength = 5;

    private final AdminUserService adminUserService;
    private final SendMailService sendMailService;
    private final OpenLegEnvironment environment;

    @Autowired
    public AdminAccountCtrl(AdminUserService adminUserService, SendMailService sendMailService,
                           OpenLegEnvironment environment) {
        this.adminUserService = adminUserService;
        this.sendMailService = sendMailService;
        this.environment = environment;
    }

    @RequiresUser
    @GetMapping(value = "/logout")
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public BaseResponse logout() {
        SecurityUtils.getSubject().logout();
        return new SimpleResponse(true, "you have been logged out", "logout");
    }

    @RequiresPermissions("admin:account:view")
    @GetMapping(value = "")
    public BaseResponse getAdminUsers() {
        return new ViewObjectResponse<>(ListView.of(
                adminUserService.getAdminUsers().stream().map(AdminUserView::new).toList()));
    }

    @RequiresPermissions("admin:account:view")
    @GetMapping(value = "/{username:.+}")
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
    @PostMapping(value = "/{username:.+}")
    public Object createNewUser(@PathVariable String username,
                                @RequestParam(defaultValue = "false") boolean master) {
        if (adminUserService.adminInDb(username)) {
            return new ResponseEntity<>(
                    new ViewObjectErrorResponse(ErrorCode.USER_ALREADY_EXISTS, username), HttpStatus.CONFLICT);
        }

        String password = RandomUtils.getRandomString(newPassLength);
        try {
            adminUserService.createAdmin(username, password, true, master);
        } catch (InvalidUsernameException ex) {
            throw new InvalidRequestParamEx(username, "username", "String", ex.getProperFormat());
        }
        sendNewUserEmail(username, password);
        return new SimpleResponse(true, username + " has been successfully registered as an admin user",
                "admin-registered");
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
    @DeleteMapping(value = "/{username:.+}")
    public Object removeUser(@PathVariable String username) {
        if (!adminUserService.adminInDb(username)) {
            throw new UserNotFoundException(username);
        }
        if (environment.getDefaultAdminName().equals(username)) {
            return new ResponseEntity<>(
                    new ViewObjectErrorResponse(ErrorCode.CANNOT_DELETE_ADMIN, username), HttpStatus.FORBIDDEN);
        }
        adminUserService.deleteAdmin(username);
        return new SimpleResponse(true, "The admin user " + username + " has been successfully removed",
                "admin-deleted");
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
     *  Expected Output: successful pass-changed response if the password was changed,
     *  ErrorResponse otherwise.
     */
    @RequiresPermissions("admin")
    @PostMapping(value = "/passchange")
    public Object changePassword(@RequestParam() String password) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        AdminUser user = adminUserService.getAdminUser(username);
        if (BCrypt.checkpw(password, user.getPassword())) {
            return new ResponseEntity<>(
                    new ErrorResponse(ErrorCode.SAME_PASSWORD), HttpStatus.BAD_REQUEST);
        }
        if (password.length() < minPassLength) {
            throw new InvalidRequestParamEx("*".repeat(password.length()), "password", "String",
                    "Password must contain at least " + minPassLength + " characters");
        }
        user.setPassword(password);
        adminUserService.createAdmin(user);
        return new SimpleResponse(true, "Password has been successfully changed", "pass-changed");
    }

    /** --- Exception Handlers --- */

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.USER_DOES_NOT_EXIST, ex.getUsername());
    }

    /**
     * Sends an email to a new user notifying them of their registration
     * @param username The username/email address of the new user
     * @param password the password of the new user
     */
    @Async
    protected void sendNewUserEmail(String username, String password) {
        String message = StringSubstitutor.replace(registrationEmailTemplate,
                Map.of("username", username, "password", password, "base_url",environment.getUrl()));
        sendMailService.sendMessage(username, registrationEmailSubject, message);
    }

    private static class UserException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = -6422565299854256546L;
        private final String username;

        public UserException(String message, String username) {
            super(message);
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

    private static class UserNotFoundException extends UserException {
        @Serial
        private static final long serialVersionUID = 3276041543957882445L;
        public UserNotFoundException(String username) {
            super("User " + username + " was not found!", username);
        }
    }
}
