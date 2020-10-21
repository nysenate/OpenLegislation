package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.model.NotificationSubscription;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import gov.nysenate.openleg.notifications.model.NotificationType;

public class NotificationSubscriptionView implements ViewObject
{
    protected int id;
    protected String userName;
    protected NotificationType type;
    protected NotificationMedium target;
    protected String address;

    public NotificationSubscriptionView(NotificationSubscription subscription) {
        this.id = subscription.getId();
        this.userName = subscription.getUserName();
        this.type = subscription.getNotificationType();
        this.target = subscription.getMedium();
        this.address = subscription.getTargetAddress();
    }

    public int getId() {
        return id;
    }

    public NotificationSubscriptionView setId(int id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationMedium getTarget() {
        return target;
    }

    public void setTarget(NotificationMedium target) {
        this.target = target;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getViewType() {
        return "notification-subscription";
    }
}
