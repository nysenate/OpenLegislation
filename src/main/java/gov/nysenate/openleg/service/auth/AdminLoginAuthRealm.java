package gov.nysenate.openleg.service.auth;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AdminLoginAuthRealm extends AuthorizingRealm
{
    private static final Logger logger = LoggerFactory.getLogger(AdminLoginAuthRealm.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DefaultWebSecurityManager defaultWebSecurityManager;

    @Value("${default.admin.user}") private String defaultAdminName;
    @Value("${default.admin.password}") private String defaultAdminPass;

    @PostConstruct
    public void setup() {
        defaultWebSecurityManager.setRealm(this);
        if (!adminUserService.adminInDb(defaultAdminName))
            adminUserService.createUser(defaultAdminName, defaultAdminPass);
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
        if (token != null && token instanceof UsernamePasswordToken) {
            return queryForAuthenticationInfo((UsernamePasswordToken) (token));
        }
        throw new UnsupportedTokenException("OpenLeg 2.0 only supports UsernamePasswordToken");
    }

    /**
     * This method uses the AdminUser service to query the database and see if
     * the given username and password combination
     * @param info The given UsernamePasswordToken
     * @return A new SimpleAuthenticationInfo object if the user is a valid Admin or null otherwise
     */
    protected AuthenticationInfo queryForAuthenticationInfo(UsernamePasswordToken info) {
        String username = info.getUsername();
        String password = new String(info.getPassword());

        int response = adminUserService.login(username, password);
        logger.info("Login attempt by " + username + " with given password: " +password+ " returned login"
         + " code: " +response);

        // Valid admin credentials
        if (response == 0)
            return new SimpleAuthenticationInfo(username, password, getName());
        return null;
    }

    /**
     * This method will return the Authorization Information for a particular admin
     * @param principals The identifying attributes of the currently active user
     * @return A SimpleAuthorizationInfo object containing the roles and permissions of the user.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}