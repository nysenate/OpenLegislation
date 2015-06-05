package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.service.mail.SendMailService;
import gov.nysenate.openleg.service.notification.dispatch.BaseNotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class EmailNotificationSender extends BaseNotificationSender implements NotificationSender {

    @Autowired
    private SendMailService sendMailService;

    /**
     * Does the work of addressing and sending a mail message to a number of notification subscribers
     * @param message A message to send
     * @param subscriptions A collection of notification subscribers that will receive the message
     */
    protected void sendNotificationEmail(SimpleMailMessage message, Collection<NotificationSubscription> subscriptions) {
        String[] addresses = subscriptions.stream()
                .map(NotificationSubscription::getTargetAddress)
                .collect(Collectors.toList())
                .toArray(new String[subscriptions.size()]);
        message.setTo(addresses);
        sendMailService.sendMessage(message);
    }
}
