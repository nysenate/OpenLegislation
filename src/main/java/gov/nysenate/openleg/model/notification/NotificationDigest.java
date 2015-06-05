package gov.nysenate.openleg.model.notification;

import com.google.common.collect.Range;
import gov.nysenate.openleg.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationDigest {

    /** The type of notification this digest contains */
    private NotificationType type;

    /** Contains notifications from between these dates */
    private Range<LocalDateTime> digestRange;

    /** The notifications to include in the digest */
    private List<RegisteredNotification> notifications;

    /** The medium through which the digest will be sent */
    private NotificationTarget target;

    /** The address the digest will be sent to */
    private String address;

    /** The digest will include full notification messages if set to true */
    private boolean full;

    /** --- Constructor --- */

    public NotificationDigest(NotificationType type, Range<LocalDateTime> digestRange,
                              List<RegisteredNotification> notifications, boolean full,
                              NotificationTarget target, String address) {
        this.type = type;
        this.digestRange = digestRange;
        this.notifications = notifications;
        this.target = target;
        this.address = address;
        this.full = full;
    }

    /** --- Functional Getters / Setters --- */

    public boolean isEmpty() {
        return notifications.isEmpty();
    }

    public LocalDateTime getStartDateTime() {
        return DateUtils.startOfDateTimeRange(digestRange);
    }

    public LocalDateTime getEndDateTime() {
        return DateUtils.endOfDateTimeRange(digestRange);
    }

    /** --- Getters / Setters --- */

    public NotificationType getType() {
        return type;
    }

    public Range<LocalDateTime> getDigestRange() {
        return digestRange;
    }

    public List<RegisteredNotification> getNotifications() {
        return notifications;
    }

    public NotificationTarget getTarget() {
        return target;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFull() {
        return full;
    }
}
