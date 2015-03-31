package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.slack.SlackAttachment;
import gov.nysenate.openleg.service.slack.SlackChatService;
import gov.nysenate.openleg.service.slack.SlackField;
import gov.nysenate.openleg.service.slack.SlackMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
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
        SlackMessage message = new SlackMessage()
                .addAttachments(new SlackAttachment()
                        .setTitle("Notification #" + notification.getId())
                        .setTitleLink(getDisplayUrl(notification))
                        .setPretext(notification.getSummary())
                        .setText(notification.getMessage())
                        .setFallback(truncateMessage(notification))
                        .setFields(getFields(notification))
                        .setColor(getColor(notification)))
                .setMentions(addresses.stream()
                        .map(NotificationSubscription::getTargetAddress)
                        .collect(Collectors.toList()))
                .setText("")
                .setUsername("openleg-bot")
                .setIcon(getIcon(notification));
        slackChatService.sendMessage(message);
    }

    /**
     * Truncates the notification message for slack consumption
     * @param notification RegisteredNotification
     * @return String
     */
    private String truncateMessage(RegisteredNotification notification) {
        return trimLines(notification.getMessage(), environment.getSlackLineLimit()) +
            "\nSee full notification at: " + getDisplayUrl(notification);
    }

    private ArrayList<SlackField> getFields(RegisteredNotification notification) {
        return new ArrayList<>(Arrays.asList(
                new SlackField("Type", notification.getType().toString()),
                new SlackField("Occurred",
                        notification.getOccurred().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
        ));
    }

    private String getColor(RegisteredNotification notification) {
        if (NotificationType.EXCEPTION.covers(notification.getType())) {
            return "danger";
        } else if (NotificationType.WARNING.covers(notification.getType())) {
            return "warning";
        }
        return "good";
    }

    private String getIcon(RegisteredNotification notification) {
        if (NotificationType.EXCEPTION.covers(notification.getType())) {
            return ":scream_cat:";
        }
        if (NotificationType.WARNING.covers(notification.getType())) {
            return ":crying_cat_face:";
        }
        return ":smile_cat:";
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
