package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public abstract class NotificationDigestFormatter {

    /**
     * A default way of formatting a notification digest into text
     * @param digest NotificationDigest
     * @param getUrl Function<RegisteredNotification, String> - a function that gets the url for a notification
     * @return String
     */
    public static String getDigestText(NotificationDigest digest, Function<RegisteredNotification, String> getUrl) {
        StringBuilder digestBuilder = new StringBuilder();
        digestBuilder.append("All ")
                .append(digest.getType())
                .append(" notifications from ")
                .append(digest.getStartDateTime().truncatedTo(ChronoUnit.SECONDS))
                .append(" to ")
                .append(digest.getEndDateTime().truncatedTo(ChronoUnit.SECONDS))
                .append(":\n\n------------------------------------------------------------------------------------\n");
        digest.getNotifications().forEach(notification -> {
            digestBuilder.append("Occurred: ")
                    .append(notification.getOccurred())
                    .append("\nId: ")
                    .append(notification.getId())
                    .append("\nURL: ")
                    .append(getUrl.apply(notification))
                    .append("\nType: ")
                    .append(notification.getType())
                    .append("\nSummary: ")
                    .append(notification.getSummary());
            if (digest.isFull()) {
                digestBuilder.append("\nMessage: ")
                        .append(notification.getMessage());
            }
            digestBuilder.append("\n------------------------------------------------------------------------------------\n");
        });
        return digestBuilder.toString();
    }

    /**
     * A default way of generating a summary string for a notification digest
     * @param digest NotificationDigest
     * @return String
     */
    public static String getSummary(NotificationDigest digest) {
        return "Notification Digest: " + digest.getType() +
                " from " + digest.getStartDateTime().truncatedTo(ChronoUnit.SECONDS) +
                " to " + digest.getEndDateTime().truncatedTo(ChronoUnit.SECONDS);
    }
}
