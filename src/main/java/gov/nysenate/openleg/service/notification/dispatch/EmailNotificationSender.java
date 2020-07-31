package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class EmailNotificationSender extends BaseNotificationSender implements NotificationSender {

    @Autowired private SendMailService sendMailService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotification(RegisteredNotification registeredNotification, Collection<NotificationSubscription> subscriptions) {
        SimpleMailMessage message = getNotificationMessage(registeredNotification);
        sendNotificationEmail(message, subscriptions);
    }

    /**
     * Does the work of addressing and sending a mail message to a number of notification subscribers
     * @param message A message to send
     * @param subscriptions A collection of notification subscribers that will receive the message
     */
    private void sendNotificationEmail(SimpleMailMessage message, Collection<NotificationSubscription> subscriptions) {
        String[] addresses = subscriptions.stream()
                .map(NotificationSubscription::getTargetAddress)
                .collect(Collectors.toSet())
                .toArray(new String[subscriptions.size()]);
        message.setTo(addresses);
        sendMailService.sendMessage(message);
    }

    /**
     * Generate a simple notification message that links to the full text of the notification
     */
    protected SimpleMailMessage getNotificationMessage(RegisteredNotification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(notification.getSummary());
        message.setText(getDisplayUrl(notification));
        return message;
    }
}
