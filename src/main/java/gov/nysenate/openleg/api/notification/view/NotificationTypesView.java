package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationType;

import java.util.Collection;

/**
 * Returns collections of all possible notification types and notification mediums.
 */
public class NotificationTypesView implements ViewObject {

    private Collection<NotificationType> notificationTypes;
    private Collection<NotificationMedium> notificationMediums;

    public NotificationTypesView(Collection<NotificationType> notificationTypes,
                                 Collection<NotificationMedium> notificationMediums) {
        this.notificationTypes = notificationTypes;
        this.notificationMediums = notificationMediums;
    }

    public Collection<NotificationType> getNotificationTypes() {
        return notificationTypes;
    }

    public Collection<NotificationMedium> getNotificationMediums() {
        return notificationMediums;
    }

    @Override
    public String getViewType() {
        return "notification-types";
    }
}
