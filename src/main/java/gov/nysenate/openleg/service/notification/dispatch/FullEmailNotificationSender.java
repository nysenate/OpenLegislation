package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationMedium;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class FullEmailNotificationSender extends EmailNotificationSender {

    @Override
    public NotificationMedium getTargetType() {
        return NotificationMedium.EMAIL;
    }

    /**
     * Override to include full notification text in the message
     */
    @Override
    protected SimpleMailMessage getNotificationMessage(RegisteredNotification notification) {
        SimpleMailMessage message = super.getNotificationMessage(notification);
        message.setText(notification.getMessage());
        return message;
    }
}
