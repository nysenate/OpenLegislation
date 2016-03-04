package gov.nysenate.openleg.service.notification.data;

import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationDigestSubscription;
import gov.nysenate.openleg.model.search.SearchException;

/**
 * Constructs a notification digest based on notification digest subscriptions
 */
public interface NotificationDigestService {

    /**
     * Constructs a notification digest based on the given notification digest subscription
     * by searching for notifications of the subscribed type, for the subscribed time period
     * @param subscription NotificationDigestSubscription
     * @return NotificationDigest
     */
    NotificationDigest getDigest(NotificationDigestSubscription subscription) throws SearchException;
}
