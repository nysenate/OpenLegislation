package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.service.slack.SlackAttachment;
import gov.nysenate.openleg.service.slack.SlackField;
import gov.nysenate.openleg.service.slack.SlackMessage;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlackNotificationSender extends BaseSlackNotificationSender implements NotificationSender {

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
    public void sendNotification(RegisteredNotification notification, Collection<NotificationSubscription> subscriptions) {
        SlackMessage message = new SlackMessage()
                .addAttachments(new SlackAttachment()
                        .setTitle("Notification #" + notification.getId())
                        .setTitleLink(getDisplayUrl(notification))
                        .setPretext(notification.getSummary())
                        .setText(notification.getMessage())
                        .setFallback(truncateNotification(notification))
                        .setFields(getFields(notification))
                        .setColor(getColor(notification)))
                .setText("")
                .setIcon(getIcon(notification));
        List<String> addresses = subscriptions.stream()
                        .map(NotificationSubscription::getTargetAddress)
                        .collect(Collectors.toList());
        slackChatService.sendMessage(message, addresses);
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
        } else if (NotificationType.WARNING.covers(notification.getType()) ||
                   NotificationType.SPOTCHECK_MISMATCH.covers(notification.getType())) {
            return "warning";
        }
        return "good";
    }

    private String getIcon(RegisteredNotification notification) {
        if (NotificationType.EXCEPTION.covers(notification.getType())) {
            return ":scream_cat:";
        } else if (NotificationType.WARNING.covers(notification.getType())) {
            return ":pouting_cat:";
        } else if (NotificationType.SPOTCHECK_MISMATCH.covers(notification.getType())) {
            return ":see_no_evil:";
        }
        return ":smile_cat:";
    }
}
