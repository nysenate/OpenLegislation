package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.slack.SlackChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SlackNotificationSender implements NotificationSender {

    @Autowired
    private SlackChatService slackChatService;

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationTarget getTargetType() {
        return NotificationTarget.SLACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotification(RegisteredNotification notification, Collection<NotificationSubscription> addresses) {
        slackChatService.sendMessage(notification.getMessage(),
                addresses.stream()
                        .map(NotificationSubscription::getTargetAddress)
                        .collect(Collectors.toList()));

    }
}
