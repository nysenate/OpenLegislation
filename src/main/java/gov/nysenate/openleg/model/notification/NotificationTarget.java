package gov.nysenate.openleg.model.notification;

import com.google.common.collect.ImmutableSet;

public enum NotificationTarget {
    EMAIL,
    EMAIL_SIMPLE,
    SLACK;

    public static ImmutableSet<NotificationTarget> ALL_NOTIFICATION_TARGETS = ImmutableSet.copyOf(NotificationTarget.values());

    public static NotificationTarget getValue(String text) {
        return NotificationTarget.valueOf(text.toUpperCase());
    }

    public static ImmutableSet<NotificationTarget> getAllNotificationTargets() {
        return ALL_NOTIFICATION_TARGETS;
    }
}
