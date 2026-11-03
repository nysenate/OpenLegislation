package gov.nysenate.openleg.notifications;

import gov.nysenate.openleg.notifications.mail.SendMailService;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class EmailNotificationSender extends BaseNotificationSender implements NotificationSender {

    @Autowired private SendMailService sendMailService;

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationMedium getTargetType() {
        return NotificationMedium.EMAIL;
    }

    /**
     * Sends a single email, addressed to every subscriber, with the notification summary as the subject
     * and its full message as the body.
     */
    @Override
    public void sendNotification(RegisteredNotification notification, Collection<NotificationSubscription> subscriptions) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(notification.getSummary());
        message.setText(notification.getMessage());
        message.setTo(subscriptions.stream()
                .map(NotificationSubscription::getTargetAddress)
                .collect(Collectors.toSet())
                .toArray(String[]::new));
        sendMailService.sendMessage(message);
    }
}
