package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseNotificationSender {

    @Autowired
    protected Environment environment;

    private static final String notificationDisplayPath = "/api/3/admin/notifications/";

    private static final String notificationSearchPath = "/api/3/admin/notifications/%s/%s?type=%s&full=true&order=ASC";

    /**
     * Generates a url to the display page for the given notification
     * @param notification RegisteredNotification
     * @return String
     */
    protected String getDisplayUrl(RegisteredNotification notification) {
        return environment.getUrl() + notificationDisplayPath + notification.getId();
    }

    /**
     * Generates a url to a search page that will display the contents of the given notification digest
     * @param digest NotificationDigest
     * @return String
     */
    protected String getDigestUrl(NotificationDigest digest) {
        return environment.getUrl() + String.format(notificationSearchPath,
                digest.getStartDateTime(), digest.getEndDateTime(), digest.getType());
    }
}
