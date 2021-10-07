package gov.nysenate.openleg.notifications.mail;

import gov.nysenate.openleg.notifications.EmailNotificationSender;
import gov.nysenate.openleg.notifications.model.NotificationMedium;
import org.springframework.stereotype.Service;

/**
 * Uses the base functionality in {@link EmailNotificationSender}
 */
@Service
public class SimpleEmailNotificationSender extends EmailNotificationSender {

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationMedium getTargetType() {
        return NotificationMedium.EMAIL_SIMPLE;
    }

}
