package gov.nysenate.openleg.notifications.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.auth.admin.AdminUserService;
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
    @Autowired
    AdminUserService adminUserService;

    @Test
    public void breakingChangesNoSubscriptionsTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", numAdmins, numEmailsSent);
    }

    @Test
    public void breakingChangesOneSubscriberTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One email should have been sent.", 1+numAdmins, numEmailsSent);
    }

    @Test
    public void newFeaturesNoSubscriptionsTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.NEW_FEATURES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No Emails should have been sent.", numAdmins, numEmailsSent);
    }

    @Test
    public void newFeaturesOneSubscriberTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.NEW_FEATURES;

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One Email should have been sent.", 1+numAdmins, numEmailsSent);
    }

    @Test
    public void bothNoSubscribersTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", numAdmins, numEmailsSent);

    }

    @Test
    public void bothOneSubscriberTest() throws MailException {
        int numAdmins = adminUserService.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.NEW_FEATURES;
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("Only one email should have been sent.", 1+numAdmins, numEmailsSent);
    }

    @Test
    public void adminsReceiveBatchEmailTest() {
        String subject = "Test Email";
        String body = "This is a test email to verify that all admins will receive any batch email that is send out.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.BREAKING_CHANGES;
        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numAdmins = adminUserService.getAdminUsers().size();
        int numSubscribers = 1;//apiUserService.getUsersWithSubscription(sub).size();
        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("The number of emails sent out was incorrect.",
                numAdmins+numSubscribers, numEmailsSent);
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