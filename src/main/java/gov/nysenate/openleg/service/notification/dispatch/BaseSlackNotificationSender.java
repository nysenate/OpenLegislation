package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.slack.SlackAddress;
import gov.nysenate.openleg.service.slack.SlackChatService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseSlackNotificationSender extends BaseNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(BaseSlackNotificationSender.class);

    private static final Pattern slackAddressPattern =
            Pattern.compile("^(?:#([a-z-]+))?(?:@([a-z]*))?$");

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

    protected SlackAddress parseAddress(String addressString) {
        Matcher addressMatcher = slackAddressPattern.matcher(addressString);
        if (!addressMatcher.matches()) {
            logger.error("addressString: '{}' doesn't conform to pattern: {}",
                    addressString, slackAddressPattern.pattern());
            return new SlackAddress();
        }
        String channel = addressMatcher.group(1) != null
                ? addressMatcher.group(1)
                : SlackAddress.DEFAULT_CHANNEL_ID;
        String username = addressMatcher.group(2);
        return new SlackAddress(channel, username);
    }
}
