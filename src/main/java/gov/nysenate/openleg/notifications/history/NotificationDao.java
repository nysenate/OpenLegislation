package gov.nysenate.openleg.notifications.history;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A record of every notification that has occurred.
 */
public interface NotificationDao {

    RegisteredNotification getNotification(long notificationId) throws NotificationNotFoundException;

    /**
     * Retrieves the notifications of the given types that occurred within the date time range,
     * ordered by when they occurred.
     */
    PaginatedList<RegisteredNotification> getNotifications(Set<NotificationType> types,
                                                           Range<LocalDateTime> dateTimeRange,
                                                           SortOrder order, LimitOffset limitOffset);

    /**
     * Stores a notification, assigning it a new id.
     */
    RegisteredNotification registerNotification(Notification notification);
}
