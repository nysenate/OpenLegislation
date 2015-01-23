package gov.nysenate.openleg.service.notification;

import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.slack.SlackChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SlackNotificationSender extends BaseNotificationSender {

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
        slackChatService.sendMessage(prepareMessage(notification),
                addresses.stream()
                        .map(NotificationSubscription::getTargetAddress)
                        .collect(Collectors.toList()));

    }

    /**
     * Tailors the notification message for slack consumption
     * @param notification RegisteredNotification
     * @return String
     */
    private String prepareMessage(RegisteredNotification notification) {
        return trimLines(notification.getMessage(), environment.getSlackLineLimit()) +
            "\nSee full notification at: " + getDisplayUrl(notification);
    }

    private String trimLines(String str, int maxLength) {
        String[] lines = StringUtils.split(str, "\n");
        if (lines.length <= maxLength) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(lines[0]);
        for (int i = 1; i < maxLength; i++) {
            builder.append("\n").append(lines[i]);
        }
        builder.append("...");
        return builder.toString();
    }
}
