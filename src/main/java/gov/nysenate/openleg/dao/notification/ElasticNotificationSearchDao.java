package gov.nysenate.openleg.dao.notification;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.client.view.notification.NotificationView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.search.ClearIndexEvent;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ElasticNotificationSearchDao extends ElasticBaseDao implements NotificationSearchDao, IndexedSearchService<RegisteredNotification> {

    Logger logger = LoggerFactory.getLogger(ElasticNotificationSearchDao.class);

    protected static final String notificationIndex = SearchIndex.NOTIFICATION.getIndexName();
    protected static final String notificationType = "notifications";
    protected static final String idType = "id";
    protected static final String idId = "id";

    /** --- Implemented Methods --- */

    @Override
    public Optional<RegisteredNotification> getNotification(long notificationId) {
        return getRequest(notificationIndex, notificationType, Long.toString(notificationId),
                getResponse -> getNotificationFromSourceMap(getResponse.getSource()));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> searchNotifications(QueryBuilder query, FilterBuilder filter,
                                                                     List<SortBuilder> sort, LimitOffset limitOffset) {
        // Restrict search to only notifications, excluding the id incrementer
        FilterBuilder fullFilter = FilterBuilders.andFilter(filter, FilterBuilders.typeFilter(notificationType));
        SearchRequestBuilder request = getSearchRequest(notificationIndex, query, fullFilter, null, null, sort, limitOffset, true);
        SearchResponse response = request.execute().actionGet();
        return getSearchResults(response, limitOffset, hit -> getNotificationFromSourceMap(hit.getSource()));
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        RegisteredNotification regNotification = new RegisteredNotification(notification, getNextId());
        searchClient.prepareIndex(notificationIndex, notificationType, Long.toString(regNotification.getId()))
                .setSource(OutputUtils.toJson(new NotificationView(regNotification)))
                .execute().actionGet();
        return regNotification;
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getIndices() {
        return Collections.singletonList(notificationIndex);
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

    /** --- Internal Methods --- */

    /**
     * Gets the next available notification id by indexing to id/id and returning the version from the response
     * @return long - the next available notification id
     */
    protected long getNextId() {
        IndexResponse response = searchClient.prepareIndex(notificationIndex, idType, idId)
                .setSource("{}").execute().actionGet();
        return response.getVersion();
    }

    protected RegisteredNotification getNotificationFromSourceMap(Map<String, Object> source) {
        long id = Long.parseLong(source.get("id").toString());
        NotificationType type = NotificationType.getValue(source.get("notificationType").toString());
        LocalDateTime occurred = LocalDateTime.parse(source.get("occurred").toString());
        String summary = source.get("summary") != null ? source.get("summary").toString() : "";
        String message = source.get("message") != null ? source.get("message").toString() : "";
        return new RegisteredNotification(id, type, occurred, summary, message);
    }
}
