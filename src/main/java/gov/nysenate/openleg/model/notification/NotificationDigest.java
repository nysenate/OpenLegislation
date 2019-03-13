package gov.nysenate.openleg.model.notification;

import com.google.common.collect.Range;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationDigest {

    /** The type of notification this digest contains */
    private NotificationType type;

    /** Contains notifications generated after this time */
    private LocalDateTime startDateTime;

    /** Contains notifications up to this time */
    private LocalDateTime endDateTime;

    /** The notifications to include in the digest */
    private List<RegisteredNotification> notifications;

    /** The medium through which the digest will be sent */
    private NotificationMedium medium;

    /** The address the digest will be sent to */
    private String address;

    /** The digest will include full notification messages if set to true */
    private boolean full;

    /** --- Constructor --- */

    public NotificationDigest(NotificationType type, LocalDateTime startDateTime, LocalDateTime endDateTime,
                              List<RegisteredNotification> notifications, boolean full,
                              NotificationMedium medium, String address) {
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.notifications = notifications;
        this.medium = medium;
        this.address = address;
        this.full = full;
    }

    /** --- Functional Getters / Setters --- */

    public boolean isEmpty() {
        return notifications.isEmpty();
    }

    public Range<LocalDateTime> getDigestRange() {
        return Range.openClosed(startDateTime, endDateTime);
    }

    /** --- Getters / Setters --- */

    public NotificationType getType() {
        return type;
    }

    public List<RegisteredNotification> getNotifications() {
        return notifications;
    }

    public NotificationMedium getMedium() {
        return medium;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFull() {
        return full;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}
