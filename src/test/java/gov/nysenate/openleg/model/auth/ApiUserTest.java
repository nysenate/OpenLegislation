package gov.nysenate.openleg.model.auth;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.service.auth.OpenLegRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;


@Category(UnitTest.class)
public class ApiUserTest {

    String sub1 = "BREAKING_CHANGES";
    String sub2 = "NEW_FEATURES";
    String email = "bogusBunny@nysenate.gov";
    ApiUser apiUser = new ApiUser(email);

    @Before
    public void setup() {
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
    }

    @Test
    public void addSubscriptionToApiUserTest() {
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        ImmutableSet<ApiUserSubscriptionType> subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 1);
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub2));
        subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 2.", subs.size(), 2);
    }

    @Test
    public void removeSubscriptionFromApiUserTest() {
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub2));
        apiUser.removeSubscription(ApiUserSubscriptionType.valueOf(sub1));
        ImmutableSet<ApiUserSubscriptionType> subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 1);
        apiUser.removeSubscription(ApiUserSubscriptionType.valueOf(sub2));
        subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 0);
    }

    @Test
    public void rolesTest() {
        for (OpenLegRole r : OpenLegRole.values())
            apiUser.addRole(r);
        assertEquals(apiUser.getGrantedRoles().size(), OpenLegRole.values().length);
        for (OpenLegRole r : OpenLegRole.values())
            apiUser.removeRole(r);
        assertTrue(apiUser.getGrantedRoles().isEmpty());
    }

    @Test
    public void subscriptionsTest() {
        for (ApiUserSubscriptionType t : ApiUserSubscriptionType.values())
            apiUser.addSubscription(t);
        assertEquals(apiUser.getSubscriptions().size(), ApiUserSubscriptionType.values().length);
        for (ApiUserSubscriptionType t : ApiUserSubscriptionType.values())
            apiUser.removeSubscription(t);
        assertTrue(apiUser.getSubscriptions().isEmpty());
    }

    @Test
    public void hashCodeTest() {
        ApiUser secondUser = new ApiUser(apiUser.getEmail());
        secondUser.setApiKey(apiUser.getApiKey());
        assertEquals(apiUser.hashCode(), secondUser.hashCode());
        assertEquals(apiUser, secondUser);
    }
}