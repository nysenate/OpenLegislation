package gov.nysenate.openleg.service.notification.dispatch;

import gov.nysenate.openleg.model.notification.NotificationMedium;
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
