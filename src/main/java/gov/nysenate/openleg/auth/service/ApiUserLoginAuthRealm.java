package gov.nysenate.openleg.auth.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.auth.model.ApiKeyLoginToken;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.auth.user.ApiUserAuthEvictEvent;
import gov.nysenate.openleg.auth.user.ApiUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
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

import java.util.Collection;
import java.util.List;

@Component
public class ApiUserLoginAuthRealm extends OpenLegAuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserLoginAuthRealm.class);
    private static final CredentialsMatcher apiCredentialsMatcher = (token, info) -> {
        try {
            return StringUtils.equals(token.getPrincipal().toString(),
                    info.getPrincipals().getPrimaryPrincipal().toString());
        }
        catch (NullPointerException e) {
            return false;
        }
    };

    private final ApiUserService apiUserService;
    private final EventBus eventBus;

    @Autowired
    public ApiUserLoginAuthRealm(ApiUserService apiUserService, EventBus eventBus) {
        this.apiUserService = apiUserService;
        this.eventBus = eventBus;
    }

    @Override
    public void onInit() {
        super.onInit();
        eventBus.register(this);
    }

    /**
     * Check to see if the API Key exists. If it does return the AuthenticationInfo.
     * @param token The given authentication information
     * @return AuthenticationInfo for the given token, or null if the account is not valid
     * @throws UnsupportedTokenException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token instanceof ApiKeyLoginToken apiToken) {
            logger.debug("Attempting login with API Realm from {} with key {}",
                    apiToken.getHost(), apiToken.getApiKey());
            if (apiUserService.validateKey(apiToken.getApiKey())) {
                return new SimpleAuthenticationInfo(apiToken.getApiKey(), apiToken.getApiKey(),
                        this.getName());
            }
            return null;
        }
        throw new UnsupportedTokenException("OpenLeg 2.0 only supports UsernamePasswordToken");
    }

    /**
     * This method assigns the API User role.
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
        Cache<Object, AuthorizationInfo> authCache = getAuthorizationCache();
        List<Object> keyList = authCache.keys().stream()
                .filter(principals -> StringUtils.equals(principals.toString(), e.apiKey()))
                .toList();
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