package gov.nysenate.openleg.model.notification;

import com.google.common.collect.ImmutableSet;

/**
 * Media that can be used to sent notifications.
 */
public enum NotificationMedium {
    EMAIL,
    EMAIL_SIMPLE,
    SLACK;

    private static ImmutableSet<NotificationMedium> ALL_NOTIFICATION_MEDIA = ImmutableSet.copyOf(NotificationMedium.values());

    public static NotificationMedium getValue(String text) {
        return NotificationMedium.valueOf(text.toUpperCase());
    }

    public static ImmutableSet<NotificationMedium> getAllNotificationMedia() {
        return ALL_NOTIFICATION_MEDIA;
    }
}
