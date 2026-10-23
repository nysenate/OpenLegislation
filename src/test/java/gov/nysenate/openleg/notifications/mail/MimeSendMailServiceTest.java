package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.common.util.MailUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.config.annotation.UnitTest;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class MimeSendMailServiceTest {
    private MimeSendMailService sender;

    @Before
    public void setUp() throws Exception {
        Session session = Session.getInstance(new Properties());
        FakeTransport.install(session);
        FakeTransport.reset();
        MailUtils mailUtils = mock(MailUtils.class);
        when(mailUtils.getSmtpSession()).thenReturn(session);
        sender = new MimeSendMailService(mailUtils, mock(OpenLegEnvironment.class));
    }

    @Test
    public void explicitMessageIdIsKept() throws Exception {
        MimeMessage message = sender.createMessage();
        message.setFrom(new InternetAddress("openleg@example.org"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("a@example.com"));
        message.setText("Body");
        message.setHeader("Message-ID", "<notice-1@example.org>");

        assertEquals(SendResult.SENT, sender.trySend(message));

        assertEquals("<notice-1@example.org>", ((MimeMessage) FakeTransport.deliveredMessages.get(0)).getMessageID());
    }
}
