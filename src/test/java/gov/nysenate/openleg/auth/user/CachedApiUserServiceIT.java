package gov.nysenate.openleg.auth.user;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.auth.model.ApiUser;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class CachedApiUserServiceIT extends BaseTests {

    @Autowired protected ApiUserService apiUserService;

    /* Assert that a new user is still added to the database properly
    * after subscription modifications */
    @Test public void registerNewUserTest() {
        String name = "Bugs";
        String email = "bogusBunny@nysenate.gov";
        String orgName = "Org";
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(ApiUserSubscriptionType.BREAKING_CHANGES);
        subscriptions.add(ApiUserSubscriptionType.NEW_FEATURES);


        ApiUser user = apiUserService.registerNewUser(email, name, orgName, subscriptions);
        String apiKey = user.getApiKey();

        assertEquals("User was not added to database (e-mail check failed)",
                email, user.getEmail());

        //check that the subscriptions were added to the user
        assertEquals("User subscriptions were added improperly (incorrect number of subscriptions)",
               2 , apiUserService.getSubscriptions(apiKey).size());
    }

    /* Assert that the user is still being added correctly to the database
    * after subscription modifications */
    @Test public void getUserTest() {
        String name = "Bugs";
        String email = "bogusBunny@nysenate.gov";
        String orgName = "Org";
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(ApiUserSubscriptionType.BREAKING_CHANGES);
        subscriptions.add(ApiUserSubscriptionType.NEW_FEATURES);

        ApiUser user = apiUserService.registerNewUser(email, name, orgName, subscriptions);
        ApiUser savedUser = apiUserService.getUser(email);

        assertEquals("getUser() failed; user was not added to the database",
                user.getName(), savedUser.getName());
    }

    /* Assert that getSubscriptions() returns the subscriptions included
    on registration*/
    @Test public void getSubscriptionsTest() {
        String name = "Bugs";
        String email = "bogusBunny@nysenate.gov";
        String orgName = "Org";
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(ApiUserSubscriptionType.BREAKING_CHANGES);
        subscriptions.add(ApiUserSubscriptionType.NEW_FEATURES);

        ApiUser user = apiUserService.registerNewUser(email, name, orgName, subscriptions);
        String apiKey = user.getApiKey();

        ImmutableSet<ApiUserSubscriptionType> saved_subscriptions = apiUserService.getSubscriptions(apiKey);
        assertEquals("getSubscription() gives wrong number of subscriptions. ",
                2, saved_subscriptions.size());
    }

    /* Assert that addSubscription properly adds a subscription for the
    * user in the database */
    @Test public void addSubscriptionTest() {
        String name = "Bugs";
        String email = "bogusBunny@nysenate.gov";
        String orgName = "Org";
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();

        ApiUser user = apiUserService.registerNewUser(email, name, orgName, subscriptions);
        String apiKey = user.getApiKey();
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;

        assertEquals("getSubscriptions() should return an empty list",
                0, apiUserService.getSubscriptions(apiKey).size());

        //add the first subscription
        apiUserService.addSubscription(apiKey,sub1);
        assertEquals("getSubscriptions() should return a list with one element",
                1, apiUserService.getSubscriptions(apiKey).size());
        assertTrue("getSubscription should return a list with 'BREAKING_CHANGES'",
                apiUserService.getSubscriptions(apiKey).contains(sub1));

        //add the second subscription
        apiUserService.addSubscription(apiKey,sub2);
        assertEquals("getSubscriptions() should return a list with one element",
                2, apiUserService.getSubscriptions(apiKey).size());
        assertTrue("getSubscription should return a list with 'BREAKING_CHANGES'",
                apiUserService.getSubscriptions(apiKey).contains(sub1));
        assertTrue("getSubscription should return a list with 'NEW_FEATURES'",
                apiUserService.getSubscriptions(apiKey).contains(sub2));
    }

    /* Assert that removeSubscription properly removes a subscription for
    the user from the database */
    @Test public void removeSubscription() {
        String name = "Bugs";
        String email = "bogusBunny@nysenate.gov";
        String orgName = "Org";
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();

        ApiUser user = apiUserService.registerNewUser(email, name, orgName, subscriptions);
        String apiKey = user.getApiKey();
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;

        //add the subscriptions
        apiUserService.addSubscription(apiKey,sub1);
        apiUserService.addSubscription(apiKey,sub2);

        //remove the first subscription (breaking changes)
        apiUserService.removeSubscription(apiKey, sub1);
        assertEquals("getSubscriptions() should return a list with one element",
                1, apiUserService.getSubscriptions(apiKey).size());
        assertTrue("getSubscription should return a list with 'NEW FEATURES'",
                apiUserService.getSubscriptions(apiKey).contains(sub2));

        //remove the second subscription (new features)
        apiUserService.removeSubscription(apiKey, sub2);
        assertEquals("getSubscriptions() should return an empty list",
                0, apiUserService.getSubscriptions(apiKey).size());
    }
}