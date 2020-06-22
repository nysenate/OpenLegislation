package gov.nysenate.openleg.service.notification.dispatch;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.model.slack.SlackAddress;
import gov.nysenate.openleg.model.slack.SlackAttachment;
import gov.nysenate.openleg.model.slack.SlackField;
import gov.nysenate.openleg.model.slack.SlackMessage;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlackNotificationSender extends BaseSlackNotificationSender implements NotificationSender {

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationMedium getTargetType() {
        return NotificationMedium.SLACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotification(RegisteredNotification notification, Collection<NotificationSubscription> subscriptions) {
        SlackMessage message = new SlackMessage()
                .addAttachments(new SlackAttachment()
                        .setTitle("Notification #" + notification.getId())
                        .setTitleLink(getDisplayUrl(notification))
                        .setPretext(notification.getSummary())
                        .setText(notification.getMessage())
                        .setFallback(truncateNotification(notification))
                        .setFields(getFields(notification))
                        .setColor(getColor(notification.getNotificationType())))
                .setText("")
                .setIcon(getIcon(notification.getNotificationType()));
        Set<SlackAddress> addresses = subscriptions.stream()
                .map(NotificationSubscription::getTargetAddress)
                .map(this::parseAddress)
                .collect(Collectors.toSet());
        slackChatService.sendMessage(message, addresses);
    }

    /* --- Internal Methods --- */

    private ArrayList<SlackField> getFields(RegisteredNotification notification) {
        return new ArrayList<>(Arrays.asList(
                new SlackField("Type", notification.getNotificationType().toString()),
                new SlackField("Occurred",
                        notification.getOccurred().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
        ));
    }

    private String getColor(NotificationType type) {
        if (type.getUrgency().equals(NotificationUrgency.ERROR)) {
            return "danger";
        }
        else if (type.getUrgency().equals(NotificationUrgency.WARNING)) {

            return "warning";
        }
        else {
            return "good";
        }
    }

    private String getIcon(NotificationType type) {
        if (type.getUrgency().equals(NotificationUrgency.ERROR)) {
            return ":scream_cat:";
        } else if (type.getUrgency().equals(NotificationUrgency.WARNING)) {
            return ":pouting_cat:";
        } else {
            return ":smile_cat:";
        }
    }
}
