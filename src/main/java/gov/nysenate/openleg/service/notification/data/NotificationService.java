package gov.nysenate.openleg.service.notification.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;

import java.time.LocalDateTime;
import java.util.Set;

public interface NotificationService {

    /**
     * Retrieves a notification using its unique notification id
     * @param notificationId int
     * @return RegisteredNotification
     */
    public RegisteredNotification getNotification(long notificationId) throws NotificationNotFoundException;

    /**
     * Retrieves a list of notifications that occurred within the specified date time range and match one of the given
     *  notification types.  Results are order by date according to the given sort order and paginated according to
     *  the given limit / offset.
     * @param types Set<NotificationType>
     * @param dateTimeRange Range<LocalDateTime>
     * @param order SortOrder
     * @param limitOffset LimitOffset
     * @return PaginatedList<RegisteredNotification>
     */
    public PaginatedList<RegisteredNotification> getNotificationList(Set<NotificationType> types, Range<LocalDateTime> dateTimeRange,
                                                                     SortOrder order,
                                                                     LimitOffset limitOffset) throws SearchException;

    /**
     * Performs a search across all notifications using the given query, filter, and sort string
     */
    public SearchResults<RegisteredNotification> notificationSearch(String queryString, String sort, LimitOffset limitOffset) throws SearchException;

    /**
     * Inserts a notification into the data store and assigns it a notification id, returning a registered notification
     *  returns a RegisteredNotifcation with an id of -1 if registration was unsuccessful
     * @param notification Notification
     */
    public RegisteredNotification registerNotification(Notification notification);
}