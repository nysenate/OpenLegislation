package gov.nysenate.openleg.notifications.model;

import com.google.common.base.Preconditions;
import gov.nysenate.openleg.common.util.DateUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * "Groups" notifications so that too many notifications won't get sent
 * in the case of a recurring error.
 *
 * Typically, this should only be used for Notification.Types with a ERROR category,
 * however it can be used for INFO categories also. This is because INFO categories
 * usually inform us that a scheduled process has completed - they should never get
 * out of hand with too many notifications.
 *
 * This works by grouping notifications with the same NotificationType together and only
 * sending notifications for that type in certain intervals. Notifications are sent based
 * on a log base 2 calculation. They will be sent when the count of notifications is
 * 1, 2, 4, 8, 16, 32, 64, 128...
 *
 * A notification is considered fixed when the length of time {@code period} goes by without
 * any more notifications of this type occurring.
 */
public class NotificationGroup {

    private NotificationType type;      // The type of error this group is for.
    private LocalDateTime firstSeen;    // When this error group first occurred.
    private LocalDateTime lastSeen;     // When this error most recently occurred.
    private int count;                  // How many times this error has occurred.
    private Duration period;            // The time before an error is considered fixed.

    public NotificationGroup(NotificationType type, Duration period) {
        this(type, DateUtils.LONG_AGO, DateUtils.LONG_AGO, 0, period);
    }

    public NotificationGroup(NotificationType type, LocalDateTime firstSeen, LocalDateTime lastSeen,
                             int count, Duration period) {
        this.type = Preconditions.checkNotNull(type);
        this.firstSeen = Preconditions.checkNotNull(firstSeen);
        this.lastSeen = Preconditions.checkNotNull(lastSeen);
        this.count = count;
        this.period = Preconditions.checkNotNull(period);
    }

    public NotificationType getNotificationType() {
        return this.type;
    }

    /**
     * Updates this NotificationGroup with a recent notification.
     * @param notification
     */
    public void registerNotification(Notification notification) {
        if (ChronoUnit.MINUTES.between(lastSeen, notification.getOccurred()) > period.toMinutes()) {
            // its been longer than period since last occurrence -> reset this group.
            firstSeen = notification.getOccurred();
            lastSeen = notification.getOccurred();
            count = 1;
        } else {
            lastSeen = notification.getOccurred();
            count += 1;
        }
    }

    /**
     * A notification should get sent if log2(count) is a whole number.
     *
     * Make sure to call {@Code registerNotification} before calling this method.
     * @return
     */
    public boolean shouldSendNotification() {
        if (count > 1000000) {
            // If the count somehow gets really out of hand skip this calculation because it will get slow.
            return true;
        }
        return (Math.log(count) / Math.log(2)) % 1 == 0;
    }
}
