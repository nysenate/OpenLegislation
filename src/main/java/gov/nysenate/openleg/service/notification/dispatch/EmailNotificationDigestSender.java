package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.mail.SendMailService;
import gov.nysenate.openleg.util.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailNotificationDigestSender extends BaseNotificationSender implements NotificationDigestSender {

    private static final ImmutableSet<NotificationTarget> targets =
            ImmutableSet.of(NotificationTarget.EMAIL, NotificationTarget.EMAIL_SIMPLE);

    @Autowired
    private SendMailService sendMailService;

    @Override
    public Set<NotificationTarget> getTargets() {
        return targets;
    }

    @Override
    public void sendDigest(NotificationDigest digest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(digest.getAddress());
        message.setSubject(NotificationDigestFormatter.getSummary(digest));
        message.setText(NotificationDigestFormatter.getDigestText(digest, this::getDisplayUrl));
        sendMailService.sendMessage(message);
    }
}
