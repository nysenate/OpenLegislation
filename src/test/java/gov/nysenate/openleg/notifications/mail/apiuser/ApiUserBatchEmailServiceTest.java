package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.auth.user.ApiUserService;
import gov.nysenate.openleg.auth.user.ApiUserSubscriptionType;
import gov.nysenate.openleg.notifications.mail.MailException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

@Category(SillyTest.class)
public class ApiUserBatchEmailServiceTest extends BaseTests {

    @Autowired
    ApiUserService apiUserService;
    @Autowired
    ApiUserBatchEmailServiceImpl apiUserBatchEmailService;

    @Test
    public void breakingChangesNoSubscriptionsTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("No emails should have been sent.", 0, numEmailsSent);
    }

    @Test
    public void breakingChangesOneSubscriberTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("One email should have been sent.", 1, numEmailsSent);
    }

    @Test
    public void newFeaturesNoSubscriptionsTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.NEW_FEATURES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("No Emails should have been sent.", 0, numEmailsSent);
    }

    @Test
    public void newFeaturesOneSubscriberTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.NEW_FEATURES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("One Email should have been sent.", 1, numEmailsSent);
    }

    @Test
    public void bothNoSubscribersTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("No emails should have been sent.", 0, numEmailsSent);

    }

    @Test
    public void bothOneSubscriberTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message).sent();

        assertEquals("Only one email should have been sent.", 1, numEmailsSent);
    }

    @Test
    public void sendTestMessageTest() throws MailException {
        String subject = "Test email";
        String body = "This is a test email. The purpose of this email is to make sure it is sent to the correct admin.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.NEW_FEATURES;
        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        apiUserBatchEmailService.sendTestMessage("angelinamartineau@gmail.com", message);
    }
}