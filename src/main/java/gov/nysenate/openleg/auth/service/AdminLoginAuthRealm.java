package gov.nysenate.openleg.auth.service;

import gov.nysenate.openleg.auth.admin.AdminUser;
import gov.nysenate.openleg.auth.admin.AdminUserService;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Component
public class AdminLoginAuthRealm extends OpenLegAuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(AdminLoginAuthRealm.class);
    private static final CredentialsMatcher credentialsMatcher = (token, info) ->
            BCrypt.checkpw(new String(((UsernamePasswordToken) token).getPassword()),
                    info.getCredentials().toString());

    private final AdminUserService adminUserService;
    /** The IP whitelist is used here to restrict access to admin login to internal IPs only. */
    @Value("${api.auth.ip.whitelist}") private String ipWhitelist;
    @Value("${default.admin.user}") private String defaultAdminName;
    @Value("${default.admin.password}") private String defaultAdminPass;

    @Autowired
    public AdminLoginAuthRealm(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostConstruct
    public void setup() {
        if (!adminUserService.adminInDb(defaultAdminName)) {
            logger.info("Default admin user not present in database.  " +
                    "Creating default admin according to properties...");
            adminUserService.createAdmin(defaultAdminName, defaultAdminPass, true, true);
        }
    }

    /**
     * This method will call the queryForAuthenticationInfo method in order to retrieve
     * authentication info about the given admin. If the query returns a valid admin account,
     * then this method will return an AuthenticationInfo for that admin account.
     *
     * @param token The given authentication information
     * @return Either valid AuthenticationInfo for the given token or null if the account is not valid
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token instanceof UsernamePasswordToken usernamePasswordToken) {
            logger.info("Attempting login with Admin Realm from IP {}", usernamePasswordToken.getHost());
            if (usernamePasswordToken.getHost().matches(ipWhitelist)) {
                AdminUser admin = adminUserService.getAdminUser(usernamePasswordToken.getUsername());
                return new SimpleAuthenticationInfo(admin.getUsername(), admin.getPassword(), getName());
            }
            else {
                logger.warn("Blocking admin login from unauthorized IP {}", usernamePasswordToken.getHost());
                throw new AuthenticationException("Admin login from unauthorized IP address.");
            }
        }
        throw new UnsupportedTokenException(getName() + " only supports UsernamePasswordToken");
    }

    /**
     * This method will return the Authorization Information for a particular admin
     * @param principals The identifying attributes of the currently active user
     * @return A SimpleAuthorizationInfo object containing the roles and permissions of the user.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Collection collection = principals.fromRealm(getName());
        if (!collection.isEmpty()) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            String principal = collection.iterator().next().toString();
            logger.info("Determining admin roles for {}", principal);
            if (adminUserService.isMasterAdmin(principal)) {
                info.addRole(OpenLegRole.MASTER_ADMIN.name());
            }
            return info;
        }
        return null;
    }

    /**
     * Use the BCrypt credentials matcher.
     * @return The BCrypt credentials matcher.
     */
    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return credentialsMatcher;
    }
}
