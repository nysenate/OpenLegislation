package gov.nysenate.openleg.client.view.notification;

import gov.nysenate.openleg.model.notification.RegisteredNotification;

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
