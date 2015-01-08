package gov.nysenate.openleg.dao.notification;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationDao {

    /**
     * Retrieves a notification based on its notification Id
     *
     * @param notificationId int
     * @return Notification
     */
    public RegisteredNotification getNotification(int notificationId);

    /**
     * Gets a list of Notifications that match one of the given types and fall within the given date time range
     *
     * @param types Collection<NotificationType> - filters the results based on notification type
     * @param dateTimeRange Range<LocalDateTime> dateTimeRange - filters the results based on occurred time
     * @param order SortOrder - determines the sort order of the results, sorted on occurrence time
     * @param limitOffset LimitOffset - limits the number of results
     * @return PaginatedList<Notification>
     */
    public PaginatedList<RegisteredNotification> getNotifications(Collection<NotificationType> types,
                                                        Range<LocalDateTime> dateTimeRange,
                                                        SortOrder order, LimitOffset limitOffset);

    /**
     * Inserts a notification into the data store and assigns it a notification id returning a registered notification
     * @param notification Notification
     */
    public RegisteredNotification registerNotification(Notification notification);
}
