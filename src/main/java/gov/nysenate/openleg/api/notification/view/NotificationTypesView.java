package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationType;

import java.util.Collection;

/**
 * Returns collections of all possible notification types and notification mediums.
 */
public record NotificationTypesView(Collection<NotificationType> notificationTypes,
                                    Collection<NotificationMedium> notificationMediums)
        implements ViewObject {

    @Override
    public String getViewType() {
        return "notification-types";
    }
}
