package gov.nysenate.openleg.search.notifications;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.search.SearchResults;

import java.util.List;
import java.util.Optional;

public interface NotificationSearchDao {
    /**
     * Retrieves a notification based on its numeric id
     * @param notificationId long - notification numeric id
     * @return RegisteredNotification
     */
    Optional<RegisteredNotification> getNotification(long notificationId);

    /**
     * Performs a search across all notifications using the given query, filter, and sort string
     * @param query       QueryBuilder
     * @param sort        String
     * @param limitOffset LimitOffset
     * @return SearchResults<RegisteredNotification>
     */
    SearchResults<RegisteredNotification> searchNotifications(Query query,
                                                              List<SortOptions> sort, LimitOffset limitOffset);

    /**
     * Inserts a notification into the data store and assigns it a notification id, returning a registered notification
     * @param notification Notification
     */
    RegisteredNotification registerNotification(Notification notification);
}
