package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EmailNotificationSender implements NotificationSender {

    @Autowired private SendMailService sendMailService;

    @Override
    public NotificationTarget getTargetType() {
        return NotificationTarget.EMAIL;
    }

    @Override
    public void sendNotification(RegisteredNotification registeredNotification, Collection<NotificationSubscription> subscriptions) {
        SimpleMailMessage templateEmail = new SimpleMailMessage();
        templateEmail.setSubject(registeredNotification.getSummary());
        templateEmail.setText(registeredNotification.getMessage());

        SimpleMailMessage[] messages = new SimpleMailMessage[subscriptions.size()];
        int index = 0;
        subscriptions.forEach(subscription -> {
            SimpleMailMessage message = new SimpleMailMessage(templateEmail);
            message.setTo(subscription.getTargetAddress());
            messages[index] = message;
        } );

        sendMailService.sendMessage(messages);
    }
}
