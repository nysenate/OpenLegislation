package gov.nysenate.openleg.client.view.notification;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.notification.InstantNotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.ScheduledNotificationSubscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserNotificationSubscriptionsView implements ViewObject {

    private List<InstantNotificationSubscriptionView> instantSubscriptions;
    private List<ScheduledNotificationSubscriptionView> scheduledSubscriptions;

    public UserNotificationSubscriptionsView(Collection<NotificationSubscription> subscriptions) {
        instantSubscriptions = new ArrayList<>();
        scheduledSubscriptions = new ArrayList<>();

        for (NotificationSubscription sub : subscriptions) {
            if (sub instanceof InstantNotificationSubscription) {
                instantSubscriptions.add(new InstantNotificationSubscriptionView((InstantNotificationSubscription)sub));
            }
            else if (sub instanceof ScheduledNotificationSubscription) {
                scheduledSubscriptions.add(new ScheduledNotificationSubscriptionView((ScheduledNotificationSubscription)sub));
            }
        }
    }

    public List<InstantNotificationSubscriptionView> getInstantSubscriptions() {
        return instantSubscriptions;
    }

    public List<ScheduledNotificationSubscriptionView> getScheduledSubscriptions() {
        return scheduledSubscriptions;
    }

    @Override
    public String getViewType() {
        return "user-notification-subscriptions-view";
    }
}
