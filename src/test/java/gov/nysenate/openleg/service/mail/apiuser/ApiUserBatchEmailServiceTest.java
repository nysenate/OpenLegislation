package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.dao.auth.AdminUserDao;
import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.mail.MailException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

@Category(SillyTest.class)
public class ApiUserBatchEmailServiceTest extends BaseTests {

    @Autowired
    ApiUserDao apiUserDao;
    @Autowired
    ApiUserBatchEmailServiceImpl apiUserBatchEmailService;
    @Autowired
    AdminUserDao adminUserDao;

    @Test
    public void breakingChangesNoSubscriptionsTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", numAdmins, numEmailsSent);
    }

    @Test
    public void breakingChangesOneSubscriberTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One email should have been sent.", 1+numAdmins, numEmailsSent);
    }

    @Test
    public void newFeaturesNoSubscriptionsTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("NEW_FEATURES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No Emails should have been sent.", numAdmins, numEmailsSent);
    }

    @Test
    public void newFeaturesOneSubscriberTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("NEW_FEATURES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One Email should have been sent.", 1+numAdmins, numEmailsSent);
    }

    @Test
    public void bothNoSubscribersTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.valueOf("NEW_FEATURES");
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", numAdmins, numEmailsSent);

    }

    @Test
    public void bothOneSubscriberTest() throws MailException {
        int numAdmins = adminUserDao.getAdminUsers().size();
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.valueOf("NEW_FEATURES");
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
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numAdmins = adminUserDao.getAdminUsers().size();
        int numSubscribers = apiUserDao.getUsersWithSubscription(sub).size();
        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("The number of emails sent out was incorrect.",
                numAdmins+numSubscribers, numEmailsSent);
    }
}