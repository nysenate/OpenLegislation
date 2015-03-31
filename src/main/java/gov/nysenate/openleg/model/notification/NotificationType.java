package gov.nysenate.openleg.model.notification;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public enum NotificationType {

    ALL                 (AllNotifications.class),
    EXCEPTION           (ExceptionNotification.class),
    REQUEST_EXCEPTION   (RequestExceptionNotification.class),
    PROCESS_EXCEPTION   (ProcessExceptionNotification.class),
    WARNING             (WarningNotification.class),
    SPOTCHECK           (SpotcheckNotification.class),
    NEW_API_KEY         (NewApiKeyNotification.class)
    ;

    private Class<? extends AllNotifications> notificationClass;

    private static ImmutableSet<NotificationType> ALL_NOTIFICATION_TYPES = ImmutableSet.copyOf(NotificationType.values());

    private NotificationType(Class<? extends AllNotifications> notificationClass) {
        this.notificationClass = notificationClass;
    }

    /**
     * Returns true if this notification type encompasses the argument notification type
     * e.g. EXCEPTION covers REQUEST_EXCEPTION but not the other way around
     * @param other NotificationType
     * @return boolean
     */
    public boolean covers(NotificationType other) {
        return other != null && this.notificationClass.isAssignableFrom(other.notificationClass);
    }

    /**
     * Returns a set of all notification types covered by the given notification type
     * @param type NotificationType
     * @return Set<NotificationType>
     */
    public static Set<NotificationType> getCoverage(NotificationType type) {
        Set<NotificationType> result = new HashSet<>();
        if (type != null) {
            ALL_NOTIFICATION_TYPES.stream()
                    .filter(type::covers)
                    .forEach(result::add);
        }
        return result;
    }

    public static NotificationType getValue(String string) {
        return NotificationType.valueOf(StringUtils.upperCase(string));
    }

    public static ImmutableSet<NotificationType> getAllNotificationTypes() {
        return ALL_NOTIFICATION_TYPES;
    }

    /** --- Notification Classes --- */
    private static interface AllNotifications {}

    private static interface ExceptionNotification extends AllNotifications {}
    private static interface RequestExceptionNotification extends ExceptionNotification{}
    private static interface ProcessExceptionNotification extends ExceptionNotification{}

    private static interface WarningNotification extends AllNotifications {}

    private static interface SpotcheckNotification extends AllNotifications {}
    private static interface NewApiKeyNotification extends AllNotifications {}

}
