package gov.nysenate.openleg.dao.auth;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class ApiUserDaoIT extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserDaoIT.class);

    @Autowired private ApiUserDao apiUserDao;

    @Test public void insertTest() {
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        apiUserDao.insertUser(apiUser);
        ApiUser savedUser = apiUserDao.getApiUserFromEmail(email);
        assertEquals("User was not inserted properly.",apiUser.getEmail(), savedUser.getEmail());
    }

    @Test public void getAllUsersTest() {
        //Add one user
        String emailOne = "bogusBunny@nysenate.gov";
        ApiUser apiUserOne = new ApiUser(emailOne);
        apiUserOne.setName("Bugs");
        apiUserOne.setRegistrationToken("ABC123");

        //Add a second user
        String emailTwo = "world@nysenate.gov";
        ApiUser apiUserTwo = new ApiUser(emailTwo);
        apiUserTwo.setName("Hello");
        apiUserTwo.setRegistrationToken("XYZ123");

        List<ApiUser> users = apiUserDao.getAllUsers();
        int numUsersBeforeInsert = users.size();

        apiUserDao.insertUser(apiUserOne);
        apiUserDao.insertUser(apiUserTwo);

        users = apiUserDao.getAllUsers();
        assertEquals("Incorrect number of users inserted.",numUsersBeforeInsert + 2, users.size());
    }


    @Test public void addSubscriptionTest() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub2));
        String apikey = apiUser.getApiKey();

        //check the subscription list of user
        ImmutableSet<ApiUserSubscriptionType> subs = apiUser.getSubscriptions();
        assertTrue(subs.contains(ApiUserSubscriptionType.valueOf(sub1)));
        assertTrue(subs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertEquals("Number of subscriptions for this user is incorrect.",2, subs.size());

        apiUserDao.insertUser(apiUser);
        apiUserDao.addSubscription(apikey, ApiUserSubscriptionType.valueOf(sub1));
        apiUserDao.addSubscription(apikey, ApiUserSubscriptionType.valueOf(sub2));

        //check that the subscription was added to the database
        ApiUser checkApiUser = apiUserDao.getApiUserFromKey(apikey);
        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkApiUser.getSubscriptions();
        assertTrue("Subscription not added properly.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
        assertTrue("Subscription not added properly.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertEquals("Number of subscriptions added for this user in database is incorrect.",
                     2, checkSubs.size());
    }

    @Test public void removeSubscription() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        ApiUser checkUser;
        ImmutableSet<ApiUserSubscriptionType> checkSubs;

        //add two subscriptions
        apiUserDao.insertUser(apiUser);
        apiUserDao.addSubscription(apikey, ApiUserSubscriptionType.valueOf(sub1));
        apiUserDao.addSubscription(apikey, ApiUserSubscriptionType.valueOf(sub2));

        //remove "BREAKING_CHANGES"
        apiUserDao.removeSubscription(apikey, ApiUserSubscriptionType.valueOf(sub1));
        checkUser = apiUserDao.getApiUserFromKey(apikey);
        checkSubs = checkUser.getSubscriptions();
        assertTrue("Subscription removed unexpectedly.",checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertFalse("Subscription was not removed.",checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
        assertEquals("Number of subscriptions removed for this user in database is incorrect.",
                     1, checkSubs.size());

        //remove "NEW_FEATURES"
        apiUserDao.removeSubscription(apikey, ApiUserSubscriptionType.valueOf(sub2));
        checkUser = apiUserDao.getApiUserFromKey(apikey);
        checkSubs = checkUser.getSubscriptions();
        assertFalse("Subscription was not removed.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertEquals("Number of subscriptions removed for this user in database is incorrect.",
                     0, checkSubs.size());
    }

    @Test public void removeSubscriptionThatDoesntExist() {
        String sub1 = "BREAKING_CHANGES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        apiUserDao.insertUser(apiUser);
        ApiUser checkUser;
        ImmutableSet<ApiUserSubscriptionType> checkSubs;

        //remove "BREAKING_CHANGES"
        apiUserDao.removeSubscription(apikey, ApiUserSubscriptionType.valueOf(sub1));
        checkUser = apiUserDao.getApiUserFromKey(apikey);
        checkSubs = checkUser.getSubscriptions();
        assertEquals("Removal of non-existent subscription failed.", 0, checkSubs.size());
    }
}