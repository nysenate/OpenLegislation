package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.auth.ApiUserDaoIT;
import gov.nysenate.openleg.model.auth.ApiUser;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@Category(SillyTest.class)
public class ApiUserMessageTest extends BaseTests {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserDaoIT.class);

    @Test
    public void constructorWithSetOfSubscriptionsTest() {
        Set<ApiUserSubscriptionType> subs = new HashSet<>();
        subs.add(ApiUserSubscriptionType.valueOf("BREAKING_CHANGES"));
        subs.add(ApiUserSubscriptionType.valueOf("NEW_FEATURES"));
        String body = "This is the body of the message.";
        String subject = "This is the subject of the message.";
        ApiUserMessage message = new ApiUserMessage(subs, body, subject);
        assertEquals("Subscriptions were not added properly.", message.getSubscriptionTypes(), subs);
    }

    @Test
    public void constructorWithSingleSubscriptionTest() {
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
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