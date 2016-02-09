package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;
import java.util.Optional;

public interface NotificationSearchDao {

    /**
     * Retrieves a notification based on its numeric id
     * @param notificationId long - notification numeric id
     * @return RegisteredNotification
     */
    public Optional<RegisteredNotification> getNotification(long notificationId) throws ElasticsearchException;

    /**
     * Performs a search across all notifications using the given query, filter, and sort string
     * @param query QueryBuilder
     * @param filter FilterBuilder
     * @param sort String
     * @param limitOffset LimitOffset
     * @return SearchResults<RegisteredNotification>
     */
    public SearchResults<RegisteredNotification> searchNotifications(QueryBuilder query, FilterBuilder filter,
                                                                     List<SortBuilder> sort, LimitOffset limitOffset);

    /**
     * Inserts a notification into the data store and assigns it a notification id, returning a registered notification
     * @param notification Notification
     */
    public RegisteredNotification registerNotification(Notification notification);
}
