package gov.nysenate.openleg.model.notification;

import java.util.Map;

public class NotificationGroups {

    private Map<NotificationType, NotificationGroup> notificationGroups;

    public NotificationGroups(Map<NotificationType, NotificationGroup> notificationGroups) {
        this.notificationGroups = notificationGroups;
    }

    public void registerNotification(Notification notification) {
        if (notificationGroups.containsKey(notification.getNotificationType())) {
            NotificationGroup group = notificationGroups.get(notification.getNotificationType());
            group.registerNotification(notification);
        }
    }

    public boolean shouldDispatch(NotificationType notificationType) {
        if (notificationGroups.containsKey(notificationType)) {
            return notificationGroups.get(notificationType).shouldSendNotification();
        }
        return true;
    }
}
