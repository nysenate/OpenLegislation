package gov.nysenate.openleg.model.auth;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;


@Category(UnitTest.class)
public class ApiUserTest {

    String sub1 = "BREAKING_CHANGES";
    String sub2 = "NEW_FEATURES";
    String email = "bogusBunny@nysenate.gov";
    ApiUser apiUser = new ApiUser(email);

    @Test
    public void addSubscriptionToApiUserTest() {
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        ImmutableSet<ApiUserSubscriptionType> subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 1);
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub2));
        subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 2.", subs.size(), 2);
    }

    @Test
    public void removeSubscriptionFromApiUserTest() {
        apiUser.setName("Bugs");
        apiUser.setRegistrationToken("ABC123");
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub1));
        apiUser.addSubscription(ApiUserSubscriptionType.valueOf(sub2));
        apiUser.removeSubscription(ApiUserSubscriptionType.valueOf(sub1));
        ImmutableSet<ApiUserSubscriptionType> subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 1);
        apiUser.removeSubscription(ApiUserSubscriptionType.valueOf(sub2));
        subs = apiUser.getSubscriptions();
        assertEquals("Number of subscriptions should be 1.", subs.size(), 0);
    }
}