package gov.nysenate.openleg.notifications;

import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
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
