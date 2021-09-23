package gov.nysenate.openleg.notifications;

import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;

import java.util.Collection;

public interface NotificationSender {

    /**
     * @return The medium through which the sender sends notifications
    */
    NotificationMedium getTargetType();

    /**
     * Sends a notification to all of the given subscriptions
     * @param registeredNotification Notification
     * @param subscriptions Collection<NotificationSubscription>
     */
    void sendNotification(RegisteredNotification registeredNotification, Collection<NotificationSubscription> subscriptions);

}
