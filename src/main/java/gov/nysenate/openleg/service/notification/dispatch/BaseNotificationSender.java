package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNotificationSender implements NotificationSender {

    @Autowired
    protected Environment environment;

    private static final String notificationDisplayPath = "/api/3/admin/notifications/";

    /**
     * Generates a url to the display page for the given notification
     * @param notification RegisteredNotification
     * @return String
     */
    protected String getDisplayUrl(RegisteredNotification notification) {
        return environment.getUrl() + notificationDisplayPath + notification.getId();
    }
}
