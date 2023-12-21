package gov.nysenate.openleg.auth.user;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class ApiUserServiceIT extends BaseTests {

    @Autowired
    private ApiUserService apiUserDao;
    private final String sub1 = "BREAKING_CHANGES";
    private final String sub2 = "NEW_FEATURES";
    private final String emailOne = "bogusBunny@nysenate.gov";
    private ApiUser apiUserOne;
    private String apiKey;

    @Before
    public void setup() {
        apiUserOne = new ApiUser(emailOne);
        apiUserOne.setName("Bugs");
        apiUserOne.setRegistrationToken("ABC123");
        apiKey = apiUserOne.getApiKey();
    }

    @Test
    public void registerTest() {
        ApiUser actualUser = apiUserDao.registerNewUser(apiUserOne.getEmail(), apiUserOne.getName(), apiUserOne.getOrganizationName(),
                apiUserOne.getSubscriptions());
        ApiUser expectedUser = apiUserDao.getUserByKey(actualUser.getApiKey()).get();
        assertEquals("User was not inserted properly.", apiUserOne.getEmail(), expectedUser.getEmail());
    }

    @Test
    public void addSubscriptionTest() {
        apiUserOne.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        apiUserOne.addSubscription(ApiUserSubscriptionType.valueOf(sub2));

        ApiUser actualUser = apiUserDao.registerNewUser(apiUserOne.getEmail(), apiUserOne.getName(), apiUserOne.getOrganizationName(),
                apiUserOne.getSubscriptions());

        //check that the subscription was added to the database
        ApiUser checkApiUser = apiUserDao.getUserByKey(actualUser.getApiKey()).get();
        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkApiUser.getSubscriptions();
        assertTrue("Subscription not added properly.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
        assertTrue("Subscription not added properly.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
        assertEquals("Number of subscriptions added for this user in database is incorrect.",
                     2, checkSubs.size());
    }

    // TODO To be revisited later


//    @Test
//    public void removeSubscription() {
//        //add two subscriptions
//        apiUserDao.registerNewUser(apiUserOne.email(), apiUserOne.name(), apiUserOne.getOrganizationName(),
//                apiUserOne.getSubscriptions());
//        apiUserDao.addSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub1));
//        apiUserDao.addSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub2));
//
//        //remove "BREAKING_CHANGES"
//        apiUserDao.removeSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub1));
//        ApiUser checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
//        assertTrue("Subscription removed unexpectedly.",checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
//        assertFalse("Subscription was not removed.",checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
//        assertEquals("Number of subscriptions removed for this user in database is incorrect.",
//                     1, checkSubs.size());
//
//        //remove "NEW_FEATURES"
//        apiUserDao.removeSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub2));
//        checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        checkSubs = checkUser.getSubscriptions();
//        assertFalse("Subscription was not removed.", checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
//        assertEquals("Number of subscriptions removed for this user in database is incorrect.",
//                     0, checkSubs.size());
//    }
//
//    @Test
//    public void removeSubscriptionThatDoesntExist() {
//        apiUserDao.insertUser(apiUserOne);
//
//        //remove "BREAKING_CHANGES"
//        apiUserDao.removeSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub1));
//        ApiUser checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
//        assertEquals("Removal of non-existent subscription failed.", 0, checkSubs.size());
//    }
//
//    @Test
//    public void setSetSubscriptionsTest() {
//        Set<ApiUserSubscriptionType> subs = new HashSet<>();
//        subs.add(ApiUserSubscriptionType.valueOf(sub1));
//        subs.add(ApiUserSubscriptionType.valueOf(sub2));
//
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.setSubscriptions(apiKey, subs);
//        ApiUser checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
//
//        //user should have two subscriptions
//        assertEquals("Wrong number of user subscriptions added.", 2, checkSubs.size());
//        assertTrue("BREAKING_CHANGES is not in user subscriptions.",
//                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
//        assertTrue("NEW_FEATURES is not in user subscriptions.",
//                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
//
//    }
//
//    @Test
//    public void setSubscriptionsAlreadyExistTest() {
//        Set<ApiUserSubscriptionType> sub2Set = new HashSet<>();
//        sub2Set.add(ApiUserSubscriptionType.valueOf(sub2));
//
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.addSubscription(apiKey, ApiUserSubscriptionType.valueOf(sub1));
//        apiUserDao.setSubscriptions(apiKey, sub2Set);
//        ApiUser checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
//
//        //user should have one subscription of 'NEW_FEATURES'
//        assertEquals("Wrong number of user subscriptions returned.", 1, checkSubs.size());
//        assertTrue("NEW_FEATURES is not in user subscriptions.",
//                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub2)));
//        assertFalse("BREAKING_CHANGES should not be in user's subscriptions",
//                checkSubs.contains(ApiUserSubscriptionType.valueOf(sub1)));
//    }
//
//    /* Test deleting all current subscriptions */
//    @Test
//    public void setSubscriptionsEmptySet() {
//        Set<ApiUserSubscriptionType> subs = new HashSet<>();
//        subs.add(ApiUserSubscriptionType.valueOf(sub1));
//        subs.add(ApiUserSubscriptionType.valueOf(sub2));
//        Set<ApiUserSubscriptionType> emptySubs = new HashSet<>();
//
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.setSubscriptions(apiKey, subs);
//        apiUserDao.setSubscriptions(apiKey, emptySubs);
//        ApiUser checkUser = apiUserDao.getApiUserFromKey(apiKey);
//        ImmutableSet<ApiUserSubscriptionType> checkSubs = checkUser.getSubscriptions();
//        assertEquals("The user should have no subscriptions", 0, checkSubs.size());
//    }
//
//    /* Test getting users by subscription when two users have subscriptions */
//    @Test
//    public void getUserBySubscription() {
//        Set<ApiUserSubscriptionType> subs = new HashSet<>();
//        subs.add(ApiUserSubscriptionType.valueOf(sub1));
//        subs.add(ApiUserSubscriptionType.valueOf(sub2));
//
//        //second user
//        String emailTwo = "world@nysenate.gov";
//        ApiUser apiUserTwo = new ApiUser(emailTwo);
//        apiUserTwo.setName("Hello");
//        apiUserTwo.setRegistrationToken("XYZ123");
//        String apikeyTwo = apiUserTwo.apiKey();
//
//        //pre-conditions
//        List<ApiUser> subscribers_before = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub1));
//
//        //add the user and set their subscriptions
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.setSubscriptions(apiKey, subs);
//        apiUserDao.insertUser(apiUserTwo);
//        apiUserDao.setSubscriptions(apikeyTwo, subs);
//
//        //Get the list of users subscribed to 'BREAKING_FEATURES'
//        List<ApiUser> subscribers_after = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub1));
//        List<String> email_list = new ArrayList<>();
//        for(ApiUser user : subscribers_after) {
//            email_list.add(user.email());
//        }
//
//        assertEquals("Number of users returned is incorrect.", 2,
//                subscribers_after.size()-subscribers_before.size());
//        assertTrue("Api User Bugs was not in the returned list.",
//                email_list.contains(apiUserOne.email()));
//        assertTrue("Api User Hello was not in the returned List.",
//                email_list.contains(apiUserTwo.email()));
//
//        //Get the list of users subscribed to 'NEW_FEATURES'
//        subscribers_after = apiUserDao.getUsersWithSubscription(ApiUserSubscriptionType.valueOf(sub2));
//        email_list.clear();
//        for(ApiUser user : subscribers_after) {
//            email_list.add(user.email());
//        }
//    }
//
//    @Test
//    public void updateUserEmailTest() {
//        String newEmail = "helloworld@nysenate.gov";
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.updateEmail(apiKey, newEmail);
//
//        assertEquals("User email was not updated to the new email address.",
//                     newEmail, apiUserDao.getApiUserFromKey(apiKey).email());
//    }
//
//    @Test
//    public void updateUserEmailSameAsCurrentEmail() {
//        apiUserDao.insertUser(apiUserOne);
//        apiUserDao.updateEmail(apiKey, emailOne);
//
//        assertEquals("User email was not updated to the new email address.",
//                emailOne, apiUserDao.getApiUserFromKey(apiKey).email());
//    }
}