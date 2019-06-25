package gov.nysenate.openleg.service.mail.apiuser;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.auth.ApiUserDao;
import gov.nysenate.openleg.model.auth.ApiUserSubscriptionType;
import gov.nysenate.openleg.service.mail.MailException;
import gov.nysenate.openleg.util.MailUtils;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.*;

@Category(SillyTest.class)
public class ApiUserBatchEmailServiceTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(ApiUserBatchEmailServiceTest.class);
    @Autowired
    ApiUserDao apiUserDao;
    @Autowired
    ApiUserBatchEmailServiceImpl apiUserBatchEmailService;

    @Test
    public void breakingChangesNoSubscriptionsTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", 0, numEmailsSent);
    }

    @Test
    public void breakingChangesOneSubscriberTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One email should have been sent.", 1, numEmailsSent);
    }

    @Test
    public void newFeaturesNoSubscriptionsTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("NEW_FEATURES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No Emails should have been sent.", 0, numEmailsSent);
    }

    @Test
    public void newFeaturesOneSubscriberTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to new features.";
        ApiUserSubscriptionType sub = ApiUserSubscriptionType.valueOf("NEW_FEATURES");

        ApiUserMessage message = new ApiUserMessage(sub, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("One Email should have been sent.", 1, numEmailsSent);
    }

    @Test
    public void bothNoSubscribersTest() throws MailException {
        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.valueOf("NEW_FEATURES");
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("No emails should have been sent.", 0, numEmailsSent);

    }

    @Test
    public void bothOneSubscriberTest() throws MailException {

        String subject = "Test Email";
        String body = "This is a test email for Api Users subscribed to breaking changes and new features.";
        ApiUserSubscriptionType sub1 = ApiUserSubscriptionType.valueOf("BREAKING_CHANGES");
        ApiUserSubscriptionType sub2 = ApiUserSubscriptionType.valueOf("NEW_FEATURES");
        Set<ApiUserSubscriptionType> subscriptions = new HashSet<>();
        subscriptions.add(sub1);
        subscriptions.add(sub2);

        ApiUserMessage message = new ApiUserMessage(subscriptions, subject, body);

        int numEmailsSent = apiUserBatchEmailService.sendMessage(message);

        assertEquals("Only one email should have been sent.", 1, numEmailsSent);
    }
}