package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.service.slack.SlackChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseSlackNotificationSender extends BaseNotificationSender {

    @Autowired
    protected SlackChatService slackChatService;

    /**
     * Truncates the notification message for slack consumption
     * @param notification RegisteredNotification
     * @return String
     */
    protected String truncateNotification(RegisteredNotification notification) {
        return trimLines(notification.getMessage(), environment.getSlackLineLimit()) +
                "\nSee full notification at: " + getDisplayUrl(notification);
    }

    protected String truncateDigest(NotificationDigest digest, String digestText) {
        return trimLines(digestText, environment.getSlackLineLimit()) +
                "\nSee full digest at: " + getDigestUrl(digest);
    }

    protected String trimLines(String str, int maxLength) {
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
