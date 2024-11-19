package gov.nysenate.openleg.search.notifications;

import gov.nysenate.openleg.api.notification.view.NotificationView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;

import java.time.LocalDateTime;
import java.util.Set;

public interface NotificationService {

    /**
     * Retrieves a notification using its unique notification id
     * @param notificationId int
     * @return RegisteredNotification
     */
    RegisteredNotification getNotification(long notificationId) throws NotificationNotFoundException;

    /**
     * Retrieves a list of notifications that occurred within the specified date time range and match one of the given
     *  notification types.  Results are order by date according to the given sort order and paginated according to
     *  the given limit / offset.
     */
    PaginatedList<NotificationView> getNotificationList(Set<NotificationType> types, LocalDateTime from, LocalDateTime to,
                                                        SortOrder order, LimitOffset limitOffset) throws SearchException;

    /**
     * Performs a search across all notifications using the given query, filter, and sort string
     */
    SearchResults<NotificationView> notificationSearch(String queryStr, String sort, LimitOffset limitOffset) throws SearchException;

    /**
     * Inserts a notification into the data store and assigns it a notification id, returning a registered notification
     *  returns a RegisteredNotifcation with an id of -1 if registration was unsuccessful
     * @param notification Notification
     */
    RegisteredNotification registerNotification(Notification notification);
}