package gov.nysenate.openleg.service.auth;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.service.base.data.CachingService;
import gov.nysenate.openleg.service.mail.MimeSendMailService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CachedSqlApiUserService implements ApiUserService, CachingService<String>
{
    @Autowired
    protected ApiUserDao apiUserDao;

    @Autowired
    protected MimeSendMailService sendMailService;

    @Value("${domain.url}") private String domainUrl;

    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;

    private static final String apiUserCacheName = "apiusers";
    private EhCacheCache apiUserCache;

    private static final Logger logger = LoggerFactory.getLogger(CachedSqlApiUserService.class);

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(apiUserCacheName);
    }

    /*** --- CachingService Implementation --- */

    @Override
    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration().name(apiUserCacheName)
            .eternal(true)
            .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(cache);
        this.apiUserCache = new EhCacheCache(cache);
    }

    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(apiUserCache.getNativeCache());
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
        apiUserDao.getAllUsers().forEach(user -> apiUserCache.put(user.getApikey(), user));
    }

    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.APIUSER)) {
            warmCaches();
        }
    }

    /** --- ApiUserService Implementation --- */

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
                throw new UsernameExistsException(email);
        } catch (EmptyResultDataAccessException e) {
            // User does not exist as expected
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
    public ApiUser getUser(String email) {
        return apiUserDao.getApiUserFromEmail(email);
    }

    /**
     * Check to see if a given Apikey is valid.
     * If the key belongs to a user, and the user has activated their account
     * then this method will return true.
     *
     * @param apikey The apikey used with the call to the API
     * @return True if the key is valid and the user has activated their account.
     */
    @Override
    public boolean validateKey(String apikey) {
        // hit the cache first
        ApiUser user = null;
        user = (apiUserCache.get(apikey) != null) ? (ApiUser) apiUserCache.get(apikey).get() : null;

        // If the user is stored in the cache, then retrieve their information
        if (user != null) {
            return user.getAuthStatus();

        } else {
            // Fetch the user from the database
            try {
                user = apiUserDao.getApiUserFromKey(apikey);

            } catch (DataAccessException e) {
                return false;
            }

            // Add the user to the cache
            apiUserCache.put(user.getApikey(), user);
            return user.getAuthStatus();
        }
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
            sendNewApiUserNotification(user);
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
                  "\n\n-- NY Senate Development Team", user.getName(), domainUrl + "/register/token", user.getRegistrationToken());

         sendMailService.sendMessage(user.getEmail(), "Open Legislation API Account Registration", message);
    }

    /**
     * This method will send a user an email containing their API Key.
     * It is called whenever a user confirms their email address via their registration token.
     * @param user The user to send the API Key to.
     */
    public void sendApikeyEmail(ApiUser user) {
        String message = String.format("Hello %s,\n\n\tThank you for your interest in Open Legislation.\n\n\t" +
                "Here's your API Key:\n%s\n\n-- NY Senate Development Team", user.getName(), user.getApikey());

        sendMailService.sendMessage(user.getEmail(), "Your Open Legislation API Key", message);
    }

    /**
     * --- Internal Methods ---
     */

    private void sendNewApiUserNotification(ApiUser user) {
        boolean named = user.getName() != null;
        String summary = (named ? user.getName() : user.getEmail()) + " is now registered as an API user!";
        String message = summary + "\n" + (named ? "name: " + user.getName() + "\n" : "") +
                         (user.getOrganizationName() != null ? "organization: " + user.getOrganizationName() + "\n" : "") +
                         "email: " + user.getEmail();
        eventBus.post(new Notification(NotificationType.NEW_API_KEY, LocalDateTime.now(), summary, message));
    }
}
