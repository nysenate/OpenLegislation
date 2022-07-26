package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.NotificationType;

public record NotificationSubscriptionView(int id, String userName, NotificationType type,
                                          NotificationMedium target, String address)
        implements ViewObject {

    public NotificationSubscriptionView(NotificationSubscription subscription) {
        this(subscription.getId(), subscription.getUserName(), subscription.getNotificationType(),
                subscription.getMedium(), subscription.getTargetAddress());
    }

    @Override
    public String getViewType() {
        return "notification-subscription";
    }
}
