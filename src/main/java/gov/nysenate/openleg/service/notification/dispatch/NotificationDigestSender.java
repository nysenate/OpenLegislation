package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.RegisteredNotification;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Set;
import java.util.function.Function;

/** A service that will send a notification digest to certain target types */
public interface NotificationDigestSender {

    /**
     * @return the notification targets that this sender serves
     */
    Set<NotificationTarget> getTargets();

    /**
     * Formats and sends the given notification digest
     */
    void sendDigest(NotificationDigest digest);
}
