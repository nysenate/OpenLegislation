package gov.nysenate.openleg.auth.user;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.auth.exception.UsernameExistsException;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.auth.model.OpenLegRole;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.legislation.*;
import gov.nysenate.openleg.notifications.mail.SendMailService;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CachedSqlApiUserService implements ApiUserService, CachingService<String>
{
    protected final SqlApiUserDao apiUserDao;
    protected final SendMailService sendMailService;

    private long apiUserCacheSizeMb;

    private final EventBus eventBus;
    private final CacheManager cacheManager;
    private final Environment environment;

    private EhCacheCache apiUserCache;

    private static final Logger logger = LoggerFactory.getLogger(CachedSqlApiUserService.class);

    public CachedSqlApiUserService(SqlApiUserDao apiUserDao, SendMailService sendMailService, CacheManager cacheManager,
                                   EventBus eventBus, Environment environment,
                                   @Value("${api_user.cache.heap.size}") long apiUserCacheSizeMb) {
        this.apiUserDao = apiUserDao;
        this.sendMailService = sendMailService;
        this.cacheManager = cacheManager;
        this.environment = environment;
        this.eventBus = eventBus;
        this.apiUserCacheSizeMb = apiUserCacheSizeMb;
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.APIUSER.name());
    }

    /*** --- CachingService Implementation --- */

    @Override
    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration().name(ContentCache.APIUSER.name())
                .eternal(true)
                .maxBytesLocalHeap(apiUserCacheSizeMb, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(byteSizeOfPolicy()));
        cacheManager.addCache(cache);
        this.apiUserCache = new EhCacheCache(cache);
    }

    @Override
    public List<Ehcache> getCaches() {
        return Collections.singletonList(apiUserCache.getNativeCache());
    }

    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.APIUSER)) {
            evictCaches();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<String> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.APIUSER)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    @Override
    public void evictContent(String key) {
        apiUserCache.evict(key);
    }

    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up API User Cache");
        // Feed in all the api users from the database into the cache
        apiUserDao.getAllUsers().forEach(this::cacheApiUser);
    }

    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.APIUSER)) {
            warmCaches();
        }
    }

    /** --- ApiUserService Implementation --- */

    /** {@inheritDoc} */
    @Override
    public ApiUser registerNewUser(String email, String name, String orgName, Set<ApiUserSubscriptionType> subscriptions) {
        Pattern emailRegex = Pattern.compile("^[a-zA-Z\\d-._]+@[a-zA-Z\\d-._]+.[a-zA-Z]{2,4}$");
        Matcher patternMatcher = emailRegex.matcher(email);

        if (!patternMatcher.find())
            throw new IllegalArgumentException("Invalid email address format used used in registration attempt!");

        try {
            if (apiUserDao.getApiUserFromEmail(email) != null)
                throw new UsernameExistsException(email);
        } catch (EmptyResultDataAccessException e) {
            // User does not exist as expected
        }

        ApiUser newUser = new ApiUser(email);
        newUser.setName(name);
        newUser.setOrganizationName(orgName);
        newUser.setAuthenticated(false);
        newUser.setRegistrationToken(RandomStringUtils.randomAlphanumeric(32));
        newUser.setActive(true);

        // Try to send the registration email before saving the user's info into the DB.
        // If sending the email fails, the use should be able to try signing up again with the same info.
        sendRegistrationEmail(newUser);

        apiUserDao.insertUser(newUser);

        newUser.setSubscriptions(subscriptions);
        apiUserDao.updateUser(newUser);

        return newUser;
    }

    /** {@inheritDoc} */
    @Override
    public ApiUser getUser(String email) {
        return apiUserDao.getApiUserFromEmail(email);
    }

    @Override
    public void updateEmail(String apiKey, String email) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.setEmail(email);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validateKey(String apikey) {
        return getUserByKey(apikey)
                .map(ApiUser::isAuthenticated)
                .orElse(false);
    }

    /** {@inheritDoc} */
    @Override
    public void activateUser(String registrationToken) {
        try {
            ApiUser user = apiUserDao.getApiUserFromToken(registrationToken);
            if (!user.isAuthenticated()) {
                user.setActive(true);
                user.setAuthenticated(true);
                apiUserDao.updateUser(user);
                cacheApiUser(user);
                sendNewApiUserNotification(user);
            }
            sendApikeyEmail(user);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Invalid registration token supplied!");
        }
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableSet<OpenLegRole> getRoles(String key) {
        return getUserByKey(key)
                .map(ApiUser::getGrantedRoles)
                .orElse(ImmutableSet.of());
    }

    /** {@inheritDoc} */
    @Override
    public void grantRole(String apiKey, OpenLegRole role) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.addRole(role);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
        eventBus.post(new ApiUserAuthEvictEvent(apiKey));
    }

    /** {@inheritDoc} */
    @Override
    public void revokeRole(String apiKey, OpenLegRole role) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.removeRole(role);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
        eventBus.post(new ApiUserAuthEvictEvent(apiKey));
    }

    /** {@inheritDoc} */
    @Override
    public ImmutableSet<ApiUserSubscriptionType> getSubscriptions(String key) {
        return getUserByKey(key)
                .map(ApiUser::getSubscriptions)
                .orElse(ImmutableSet.of());
    }

    /** {@inheritDoc} */
    @Override
    public void addSubscription(String apiKey, ApiUserSubscriptionType subscription) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.addSubscription(subscription);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
    }

    /** {@inheritDoc} */
    @Override
    public void setSubscriptions(String apiKey, Set<ApiUserSubscriptionType> subscriptions) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.setSubscriptions(subscriptions);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
    }

    /** {@inheritDoc} */
    @Override
    public void removeSubscription(String apiKey, ApiUserSubscriptionType subscription) {
        ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
        user.removeSubscription(subscription);
        apiUserDao.updateUser(user);
        cacheApiUser(user);
    }

    /**
     * Attempt to get an api user as an optional value
     * If the user does not exist in the cache, attempt to retrieve the user from the database
     * Return an empty optional if it is not in the database
     * @param apiKey String
     * @return Optional<ApiUser>
     */
    public Optional<ApiUser> getUserByKey(String apiKey) {
        Optional<ApiUser> userOpt = getCachedApiUser(apiKey);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        try {
            ApiUser user = apiUserDao.getApiUserFromKey(apiKey);
            cacheApiUser(user);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<ApiUser> getUsersWithSubscription(ApiUserSubscriptionType subscriptionType) {
        return apiUserDao.getUsersWithSubscription(subscriptionType);
    }

    /** --- Internal Methods --- */

    /***
     * Inserts the given api user into the cache
     */
    private void cacheApiUser(ApiUser apiUser) {
        if (apiUser != null) {
            apiUserCache.put(apiUser.getApiKey(), apiUser);
        }
    }

    /**
     * Attempt to get an api user from the cache as an optional value
     * If no user with the given key exists in the cache, return an empty optional
     * @param apiKey String
     * @return Optional<ApiUser>
     */
    private Optional<ApiUser> getCachedApiUser(String apiKey) {
        return Optional.ofNullable(apiUserCache.get(apiKey))
                .map(valueWrapper -> (ApiUser) valueWrapper.get());
    }

    /**
     * This method will send a user a confirmation email, containing a link holding their registration token,
     * which will allow them to activate their account.
     *
     * @param user The user to send the registration information to
     */
    private void sendRegistrationEmail(ApiUser user) {
        final String userPlaceholder = "username";
        final String domainPlaceholder = "domain";
        final String tokenPlaceholder = "token";
        final String regEmailTemplate = "Hello ${" + userPlaceholder + "},\n\n" +
                "\tThank you for your interest in Open Legislation. " +
                "In order to receive your API key you must first activate your account by visiting the link below. " +
                "Once you have confirmed your email address, an email will be sent to you containing your API Key.\n\n" +
                "Activate your account here:\n" +
                "${" + domainPlaceholder + "}/register/${" + tokenPlaceholder + "}";
        final ImmutableMap<String, String> subMap = ImmutableMap.of(
                userPlaceholder, user.getName(),
                domainPlaceholder, environment.getUrl(),
                tokenPlaceholder, user.getRegistrationToken());
        final String message = StringSubstitutor.replace(regEmailTemplate, subMap);

        sendMailService.sendMessage(user.getEmail(), "Open Legislation API Account Registration", message);
    }

    /**
     * This method will send a user an email containing their API Key.
     * It is called whenever a user confirms their email address via their registration token.
     * @param user The user to send the API Key to.
     */
    private void sendApikeyEmail(ApiUser user) {
        final String userPlaceholder = "username";
        final String domainPlaceholder = "domain";
        final String keyPlaceholder = "apikey";
        final String fromPlaceholder = "from";
        final String keyEmailTemplate = "Hello ${" + userPlaceholder + "},\n\n" +
                "\tThank you for your interest in Open Legislation.\n\n" +
                "\tHere's your API Key:\n" +
                "${" + keyPlaceholder +"}\n\n" +
                "API documentation can be found here: ${" + domainPlaceholder + "}/docs\n" +
                "For any questions or feedback, please email us at ${" + fromPlaceholder + "}\n\n" +
                "-- NY Senate Development Team";
        final ImmutableMap<String, String> subMap = ImmutableMap.of(
                userPlaceholder, user.getName(),
                keyPlaceholder, user.getApiKey(),
                domainPlaceholder, environment.getUrl(),
                fromPlaceholder, environment.getContactEmailAddress());
        final String message = StringSubstitutor.replace(keyEmailTemplate, subMap);

        sendMailService.sendMessage(user.getEmail(), "Your Open Legislation API Key", message);
    }

    private void sendNewApiUserNotification(ApiUser user) {
        boolean named = user.getName() != null;
        String summary = (named ? user.getName() : user.getEmail()) + " is now registered as an API user!";
        String message = summary + "\n" + (named ? "name: " + user.getName() + "\n" : "") +
                         (user.getOrganizationName() != null ? "organization: " + user.getOrganizationName() + "\n" : "") +
                         "email: " + user.getEmail();
        eventBus.post(new Notification(NotificationType.NEW_API_KEY, LocalDateTime.now(), summary, message));
    }
}
