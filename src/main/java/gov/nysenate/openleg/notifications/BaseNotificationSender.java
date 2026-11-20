package gov.nysenate.openleg.notifications;

import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.model.NotificationDigest;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNotificationSender {

    @Autowired
    protected OpenLegEnvironment environment;

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
