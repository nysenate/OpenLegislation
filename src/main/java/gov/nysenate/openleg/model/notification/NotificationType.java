package gov.nysenate.openleg.model.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum NotificationType {

    ALL                 (null),
    EXCEPTION           (ALL),
    REQUEST_EXCEPTION   (EXCEPTION),
    PROCESS_EXCEPTION   (EXCEPTION),
    SPOTCHECK_EXCEPTION (EXCEPTION),
    WARNING             (ALL),
    SPOTCHECK           (ALL),
    NEW_API_KEY         (ALL),
    ;

    private NotificationType parent;

    private Set<NotificationType> children = new HashSet<>();

    private static final ImmutableSet<NotificationType> ALL_NOTIFICATION_TYPES =
            ImmutableSet.copyOf(NotificationType.values());

    NotificationType(NotificationType parent) {
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    /**
     * Returns true if this notification type encompasses the argument notification type
     * e.g. EXCEPTION covers REQUEST_EXCEPTION but not the other way around
     * @param other NotificationType
     * @return boolean
     */
    public boolean covers(NotificationType other) {
        return other != null && (this.equals(other) || covers(other.parent));
    }

    public Set<NotificationType> getChildren() {
        return children;
    }

    private Map<NotificationType, Object> getTypeHierarchy() {
        Map<NotificationType, Object> hierarchyMap = new TreeMap<>();
        children.forEach(child -> hierarchyMap.put(child, child.getTypeHierarchy()));
        return hierarchyMap;
    }

    /**
     * Returns a set of all notification types covered by the given notification type
     * @param type NotificationType
     * @return Set<NotificationType>
     */
    public static Set<NotificationType> getCoverage(NotificationType type) {
        if (type == null) return Collections.emptySet();
        Set<NotificationType> result = new HashSet<>(type.children);
        type.children.stream()
                .map(NotificationType::getCoverage)
                .forEach(result::addAll);
        return result;
    }

    public static NotificationType getValue(String string) {
        return NotificationType.valueOf(StringUtils.upperCase(string));
    }

    public static ImmutableSet<NotificationType> getAllNotificationTypes() {
        return ALL_NOTIFICATION_TYPES;
    }

    public static Map<NotificationType, Object> getHierarchy() {
        return ImmutableMap.of(ALL, ALL.getTypeHierarchy());
    }
}
