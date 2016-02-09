package gov.nysenate.openleg.service.notification.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.notification.NotificationDigest;
import gov.nysenate.openleg.model.notification.NotificationDigestSubscription;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.search.SearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ElasticNotificationDigestService implements NotificationDigestService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public NotificationDigest getDigest(NotificationDigestSubscription subscription) throws SearchException {
        Range<LocalDateTime> digestRange = Range.openClosed(subscription.getStartDateTime(), subscription.getNextDigest());
        return new NotificationDigest(subscription.getType(), digestRange,
                getNotifications(subscription.getType(), digestRange), subscription.isFull(),
                subscription.getTarget(), subscription.getTargetAddress());
    }

    /** --- Internal Methods --- */

    /**
     * Searches for notifications of the given type, that occurred within the given date time range
     */
    private List<RegisteredNotification> getNotifications(NotificationType type, Range<LocalDateTime> dateTimeRange)
            throws SearchException {
        return notificationService.getNotificationList(Collections.singleton(type), dateTimeRange,
                SortOrder.ASC, LimitOffset.ALL).getResults();
    }
}
