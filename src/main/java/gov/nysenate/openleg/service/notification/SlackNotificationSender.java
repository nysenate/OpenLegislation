package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SlackNotificationSender implements NotificationSender {

    @Override
    public NotificationTarget getTargetType() {
        return NotificationTarget.IRC;
    }

    @Override
    public void sendNotification(RegisteredNotification registeredNotification, Collection<NotificationSubscription> addresses) {

    }
}
