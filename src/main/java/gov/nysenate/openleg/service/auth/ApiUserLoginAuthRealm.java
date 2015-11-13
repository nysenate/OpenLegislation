package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.model.auth.ApiKeyLoginToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiUserLoginAuthRealm extends OpenLegAuthorizingRealm
{
    private static final Logger logger = LoggerFactory.getLogger(ApiUserLoginAuthRealm.class);

    private static final class ApiCredentialsMatcher implements CredentialsMatcher
    {
        /** The credentials will always match if the auth token and info have the same principal.*/
        @Override
        public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
            if (token != null && info != null && token.getPrincipal() != null && info.getPrincipals() != null) {
                return StringUtils.equals(token.getPrincipal().toString(),
                        info.getPrincipals().getPrimaryPrincipal().toString());
            }
            return false;
        }
    }

    private static final CredentialsMatcher apiCredentialsMatcher = new ApiCredentialsMatcher();

    @Autowired
    private ApiUserService apiUserService;

    /**
     * Check to see if the API Key exists. If it does return the AuthenticationInfo.
     *
     * @param token The given authentication information
     * @return Either valid AuthenticationInfo for the given token or null if the account is not valid
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof ApiKeyLoginToken) {
            ApiKeyLoginToken apiToken = (ApiKeyLoginToken) token;
            logger.info("Attempting login with API Realm from {} with key {}", apiToken.getHost(), apiToken.getApiKey());
            return queryForAuthenticationInfo(apiToken);
        }
        throw new UnsupportedTokenException("OpenLeg 2.0 only supports UsernamePasswordToken");
    }

    /**                                                                                            A
     * If the API Key is valid, return an AuthenticationInfo with the principal as the API key and the
     * credentials set to null. If the key is not valid, null will be returned.
     *
     * @param info The given UsernamePasswordToke
     * @return A new SimpleAuthenticationInfo object
     */
    protected AuthenticationInfo queryForAuthenticationInfo(ApiKeyLoginToken info) {
        if (apiUserService.validateKey(info.getApiKey())) {
            return new SimpleAuthenticationInfo(info.getApiKey(), info.getApiKey(), this.getName());
        }
        return null;
    }

    @Override
    public boolean isCachingEnabled() {
        return false;
    }

    /**
     * This method assigns the API User role.
     *
     * @param principals The identifying attributes of the currently active user
     * @return A SimpleAuthorizationInfo object containing the roles and permissions of the user.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole(OpenLegRole.API_USER.name());
        return info;
    }

    @Override
    public Class getAuthenticationTokenClass() {
        return ApiKeyLoginToken.class;
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return apiCredentialsMatcher;
    }
}