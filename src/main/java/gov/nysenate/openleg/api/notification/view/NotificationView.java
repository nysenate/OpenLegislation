package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.notifications.model.RegisteredNotification;

public class NotificationView extends NotificationSummaryView
{
    protected String message;

    public NotificationView(RegisteredNotification notification) {
        super(notification);
        this.message = notification.getMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
