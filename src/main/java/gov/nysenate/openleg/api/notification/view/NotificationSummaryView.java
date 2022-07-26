package gov.nysenate.openleg.api.notification.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;

import java.time.LocalDateTime;

public class NotificationSummaryView implements ViewObject {

    private final long id;
    private final NotificationType notificationType;
    private final LocalDateTime occurred;
    private final String summary;

    public NotificationSummaryView(RegisteredNotification notification) {
        this.id = notification.getId();
        this.notificationType = notification.getNotificationType();
        this.occurred = notification.getOccurred();
        this.summary = notification.getSummary();
    }

    public long getId() {
        return id;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public LocalDateTime getOccurred() {
        return occurred;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String getViewType() {
        return "notification";
    }
}
