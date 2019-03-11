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


    @Override
    public void sendDigest(NotificationDigest digest) {
        String digestText = NotificationDigestFormatter.getDigestText(digest, this::getDisplayUrl);
        SlackMessage message = new SlackMessage()
                .addAttachments(new SlackAttachment()
                        .setTitle(NotificationDigestFormatter.getSummary(digest))
                        .setTitleLink(getDigestUrl(digest))
                        .setText(digestText)
                        .setFallback(truncateDigest(digest, digestText))
                        .setColor(getColor(digest.getType()))
                        .setFields(getDigestFields(digest)))
                .setText("")
                .setIcon(getIcon(digest.getType()));
        slackChatService.sendMessage(message,
                Collections.singleton(parseAddress(digest.getAddress())));
    }

    /* --- Internal Methods --- */

    private ArrayList<SlackField> getFields(RegisteredNotification notification) {
        return new ArrayList<>(Arrays.asList(
                new SlackField("Type", notification.getNotificationType().toString()),
                new SlackField("Occurred",
                        notification.getOccurred().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
        ));
    }

    private ArrayList<SlackField> getDigestFields(NotificationDigest digest) {
        return Lists.newArrayList(
                new SlackField("Type", digest.getType().toString()),
                new SlackField("From", digest.getStartDateTime().toString()),
                new SlackField("To", digest.getEndDateTime().toString()));
    }

    private String getColor(NotificationType type) {
        if (NotificationType.EXCEPTION.covers(type)) {
            return "danger";
        } else if (NotificationType.WARNING.covers(type)) {
            return "warning";
        }
        return "good";
    }

    private String getIcon(NotificationType type) {
        if (NotificationType.EXCEPTION.covers(type)) {
            return ":scream_cat:";
        } else if (NotificationType.WARNING.covers(type)) {
            return ":pouting_cat:";
        } else if (NotificationType.SPOTCHECK.covers(type)) {
            return ":see_no_evil:";
        }
        return ":smile_cat:";
    }
}
