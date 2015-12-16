package gov.nysenate.openleg.service.auth;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.model.auth.ApiKeyLoginToken;
import gov.nysenate.openleg.model.auth.ApiUserAuthEvictEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired private ApiUserService apiUserService;

    @Autowired private EventBus eventBus;

    @Override
    public void onInit() {
        super.onInit();
        eventBus.register(this);
    }

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

    /**
     * This method assigns the API User role.
     *
     * @param principals The identifying attributes of the currently active user
     * @return A SimpleAuthorizationInfo object containing the roles and permissions of the user.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Collection principalCollection = principals.fromRealm(getName());
        if (!principalCollection.isEmpty()) {
            SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
            String apiKey = principalCollection.iterator().next().toString();
            logger.info("Assigning API_USER role to {}", apiKey);
            authInfo.addRole(OpenLegRole.API_USER.name());

            // Add any explicitly defined roles
            apiUserService.getRoles(apiKey).stream()
                    .map(OpenLegRole::name)
                    .peek(role -> logger.info("Assigning role {} to api user: {}", role, apiKey))
                    .forEach(authInfo::addRole);

            return authInfo;
        }
        return null;
    }

    @Subscribe
    public void handleApiUserAuthEvictEvent(ApiUserAuthEvictEvent e) {
        this.clearAuthForKey(e.getApiKey());
    }

    protected void clearAuthForKey(String apiKey) {
        Cache<Object, AuthorizationInfo> authCache = getAuthorizationCache();
        List<Object> keyList = authCache.keys().stream()
                .filter(principals -> StringUtils.equals(Objects.toString(principals), apiKey))
                .collect(Collectors.toList());
        keyList.forEach(authCache::remove);
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