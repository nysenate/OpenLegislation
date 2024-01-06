package gov.nysenate.openleg.search.notifications;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.search.*;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ElasticNotificationSearchDao extends ElasticBaseDao implements NotificationSearchDao, IndexedSearchService<RegisteredNotification> {
    private static final String notificationIndex = SearchIndex.NOTIFICATION.getName();
    private static final String idId = "id_counter";
    private static final QueryBuilder idIdQuery = QueryBuilders.termQuery("_id", idId);

    @Autowired
    public ElasticNotificationSearchDao(EventBus eventBus) {
        eventBus.register(this);
    }

    /* --- Implemented Methods --- */

    @Override
    public Optional<RegisteredNotification> getNotification(long notificationId) {
        return getRequest(notificationIndex, Long.toString(notificationId),
                getResponse -> getNotificationFromSourceMap(getResponse.getSource()));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> searchNotifications(QueryBuilder query, QueryBuilder filter,
                                                                     List<SortBuilder<?>> sort, LimitOffset limitOffset) {
        // Restrict search to only notifications, excluding the id incrementer
        QueryBuilder fullFilter = QueryBuilders.boolQuery().filter(filter).mustNot(idIdQuery);
        return search(notificationIndex, query, fullFilter,
                null, null,
                sort, limitOffset, true,
                hit -> getNotificationFromSourceMap(hit.getSourceAsMap()));
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        RegisteredNotification regNotification = new RegisteredNotification(notification, getNextId());
        indexJsonDoc(notificationIndex, String.valueOf(regNotification.getId()), regNotification);
        return regNotification;
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.NOTIFICATION;
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(RegisteredNotification content) {}

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<RegisteredNotification> content) {}

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        purgeIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.NOTIFICATION)) {
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.NOTIFICATION)) {
            clearIndex();
        }
    }

    /* --- Internal Methods --- */

    /**
     * Class to model the elasticsearch document that tracks the notification id field.
     */
    private static class NotificationIdCounterDoc {
        private LocalDateTime incremented = LocalDateTime.now();

        public LocalDateTime getIncremented() {
            return incremented;
        }
    }

    /**
     * Gets the next available notification id by indexing to id/id and returning the version from the response
     * @return long - the next available notification id
     */
    private long getNextId() {
        IndexResponse indexResponse = indexJsonDoc(notificationIndex, idId, new NotificationIdCounterDoc());
        return indexResponse.getVersion();
    }

    private RegisteredNotification getNotificationFromSourceMap(Map<String, Object> source) {
        long id = Long.parseLong(source.get("id").toString());
        NotificationType type = NotificationType.valueOf(StringUtils.upperCase(source.get("notificationType").toString()));
        LocalDateTime occurred = LocalDateTime.parse(source.get("occurred").toString());
        String summary = source.get("summary") != null ? source.get("summary").toString() : "";
        String message = source.get("message") != null ? source.get("message").toString() : "";
        return new RegisteredNotification(id, type, occurred, summary, message);
    }
}
