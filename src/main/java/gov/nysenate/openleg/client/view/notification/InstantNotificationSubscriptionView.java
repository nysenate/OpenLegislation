package gov.nysenate.openleg.client.view.notification;

import gov.nysenate.openleg.model.notification.InstantNotificationSubscription;

public class InstantNotificationSubscriptionView extends NotificationSubscriptionView {

    private long rateLimit;

    public InstantNotificationSubscriptionView(InstantNotificationSubscription subscription) {
        super(subscription);
        this.rateLimit = subscription.getRateLimit().toMinutes();
    }

    public long getRateLimit() {
        return rateLimit;
    }

    @Override
    public String getViewType() {
        return "instant-notification-subscription";
    }
}
