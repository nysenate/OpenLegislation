package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.notifications.model.RegisteredNotification;

public class NotificationView extends NotificationSummaryView {
    private final String message;

    public NotificationView(RegisteredNotification notification) {
        super(notification);
        this.message = notification.getMessage();
    }

    public String getMessage() {
        return message;
    }
}
