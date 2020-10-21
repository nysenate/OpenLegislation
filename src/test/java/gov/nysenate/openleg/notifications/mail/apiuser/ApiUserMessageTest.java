package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.auth.user.ApiUserServiceIT;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@Category(SillyTest.class)
public class ApiUserMessageTest extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserServiceIT.class);

    @Test
    public void constructorWithSetOfSubscriptionsTest() {
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(ApiUserSubscriptionType.BREAKING_CHANGES);
        subs.add(ApiUserSubscriptionType.NEW_FEATURES);
        String body = "This is the body of the message.";
        String subject = "This is the subject of the message.";
        ApiUserMessage message = new ApiUserMessage(subs, body, subject);
        assertEquals("Subscriptions were not added properly.", message.getSubscriptionTypes(), subs);
    }

    @Test
    public void constructorWithSingleSubscriptionTest() {
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;
        String body = "This is the body of the message.";
        String subject = "This is the subject of the message.";
        ApiUserMessage message = new ApiUserMessage(sub, body, subject);
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(sub);
        assertEquals("Subscription was not added properly.", message.getSubscriptionTypes(), subs);
    }

    @Test
    public void defaultConstructorTest() {
        ApiUserMessage message = new ApiUserMessage();
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        assertEquals(message.getSubscriptionTypes(), subs);
    }
}