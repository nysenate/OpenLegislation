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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test public void setSetSubscriptionsTest() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(ApiUserSubscriptionType.valueOf(sub1));
        subs.add(ApiUserSubscriptionType.valueOf(sub2));

        apiUserDao.insertUser(apiUser);
        apiUserDao.setSubscriptions(apikey, subs);
        ApiUser checkUser = apiUserDao.getApiUserFromKey(apikey);
        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();

        //user should have two subscriptions
        assertEquals("Wrong number of user subscriptions added.", 2, checkSubs.size());
        assertTrue("BREAKING_CHANGES is not in user subscriptions.",
                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
        assertTrue("NEW_FEATURES is not in user subscriptions.",
                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));

    }

    @Test public void setSubscriptionsAlreadyExistTest() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        Set<ApiUserSubscriptionType> sub2Set = new HashSet<>();
        sub2Set.add(ApiUserSubscriptionType.valueOf(sub2));

        apiUserDao.insertUser(apiUser);
        apiUserDao.addSubscription(apikey, ApiUserSubscriptionType.valueOf(sub1));
        apiUserDao.setSubscriptions(apikey, sub2Set);
        ApiUser checkUser = apiUserDao.getApiUserFromKey(apikey);
        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();

        //user should have one subscription of 'NEW_FEATURES'
        assertEquals("Wrong number of user subscriptions returned.", 1, checkSubs.size());
        assertTrue("NEW_FEATURES is not in user subscriptions.",
                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertFalse("BREAKING_CHANGES should not be in user's subscriptions",
                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
    }

    /* Test deleting all current subscriptions */
    @Test public void setSubscriptionsEmptySet() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(ApiUserSubscriptionType.valueOf(sub1));
        subs.add(ApiUserSubscriptionType.valueOf(sub2));
        Set<ApiUserSubscriptionType> emptySubs = new HashSet<>();

        apiUserDao.insertUser(apiUser);
        apiUserDao.setSubscriptions(apikey, subs);
        apiUserDao.setSubscriptions(apikey, emptySubs);
        ApiUser checkUser = apiUserDao.getApiUserFromKey(apikey);
        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
        assertEquals("The user should have no subscriptions", 0, checkSubs.size());
    }

    /* Test getting users by subscription when two users have subscriptions */
    @Test public void getUserBySubscription() {
        String sub1 = "BREAKING_CHANGES";
        String sub2 = "NEW_FEATURES";
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(ApiUserSubscriptionType.valueOf(sub1));
        subs.add(ApiUserSubscriptionType.valueOf(sub2));

        //first user
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String apikey = apiUser.getApiKey();

        //second user
        String emailTwo = "world@nysenate.gov";
        ApiUser apiUserTwo = new ApiUser(emailTwo);
        apiUserTwo.setName("Hello");
        apiUserTwo.setRegistrationToken("XYZ123");
        String apikeyTwo = apiUserTwo.getApiKey();

        //pre-conditions
        List<ApiUser> subscribers_before = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub1));

        //add the user and set their subscriptions
        apiUserDao.insertUser(apiUser);
        apiUserDao.setSubscriptions(apikey, subs);
        apiUserDao.insertUser(apiUserTwo);
        apiUserDao.setSubscriptions(apikeyTwo, subs);

        //Get the list of users subscribed to 'BREAKING_FEATURES'
        List<ApiUser> subscribers_after = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub1));
        List<String> email_list = new ArrayList<>();
        for(ApiUser user : subscribers_after) {
            email_list.add(user.getEmail());
        }

        assertEquals("Number of users returned is incorrect.", 2,
                subscribers_after.size()-subscribers_before.size());
        assertTrue("Api User Bugs was not in the returned list.",
                email_list.contains(apiUser.getEmail()));
        assertTrue("Api User Hello was not in the returned List.",
                email_list.contains(apiUserTwo.getEmail()));

        //Get the list of users subscribed to 'NEW_FEATURES'
        subscribers_after = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub2));
        email_list.removeAll(email_list);
        for(ApiUser user : subscribers_after) {
            email_list.add(user.getEmail());
        }
    }

    @Test
    public void updateUserEmailTest() {
        String email = "bogusBunny@nysenate.gov";
        String newEmail = "helloworld@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String key = apiUser.getApiKey();

        apiUserDao.insertUser(apiUser);
        apiUserDao.updateEmail(key, newEmail);

        assertEquals("User email was not updated to the new email address.",
                     newEmail, apiUserDao.getApiUserFromKey(key).getEmail());
    }

    @Test
    public void updateUserEmailSameAsCurrentEmail() {
        String email = "bogusBunny@nysenate.gov";
        ApiUser apiUser = new ApiUser(email);
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        String key = apiUser.getApiKey();

        apiUserDao.insertUser(apiUser);
        apiUserDao.updateEmail(key, email);

        assertEquals("User email was not updated to the new email address.",
                email, apiUserDao.getApiUserFromKey(key).getEmail());
    }
}