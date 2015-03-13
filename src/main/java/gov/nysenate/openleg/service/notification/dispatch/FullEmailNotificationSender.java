package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class FullEmailNotificationSender extends EmailNotificationSender {

    @Override
    public NotificationTarget getTargetType() {
        return NotificationTarget.EMAIL;
    }

    @Override
    public void sendNotification(RegisteredNotification registeredNotification, Collection<NotificationSubscription> subscriptions) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(registeredNotification.getSummary());
        message.setText(registeredNotification.getMessage());

        sendNotificationEmail(message, subscriptions);
    }
}
