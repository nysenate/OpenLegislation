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
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ElasticNotificationSearchDao extends ElasticBaseDao implements NotificationSearchDao, IndexedSearchService<RegisteredNotification> {

    private static final Logger logger = LoggerFactory.getLogger(ElasticNotificationSearchDao.class);

    protected static final String notificationIndex = SearchIndex.NOTIFICATION.getIndexName();
    protected static final String idId = "id";

    /** --- Implemented Methods --- */

    @Override
    public Optional<RegisteredNotification> getNotification(long notificationId) {
        return getRequest(notificationIndex, defaultType, Long.toString(notificationId),
                getResponse -> getNotificationFromSourceMap(getResponse.getSource()));
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> searchNotifications(QueryBuilder query, QueryBuilder filter,
                                                                     List<SortBuilder> sort, LimitOffset limitOffset) {
        // Restrict search to only notifications, excluding the id incrementer
        QueryBuilder fullFilter = QueryBuilders.boolQuery().filter(filter).must(QueryBuilders.typeQuery(defaultType));
        SearchRequest searchRequest = getSearchRequest(notificationIndex, query, fullFilter, null, null, sort, limitOffset, true);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchResponse = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.warn("Search Notifications request failed.", ex);
        }
        return getSearchResults(searchResponse, limitOffset,
                hit -> getNotificationFromSourceMap(hit.getSourceAsMap()));
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        RegisteredNotification regNotification = new RegisteredNotification(notification, getNextId());
        IndexRequest indexRequest = new IndexRequest(notificationIndex, defaultType, String.valueOf(regNotification.getId()))
                .source(OutputUtils.toJson(new NotificationView(regNotification)), XContentType.JSON);
        try {
            searchClient.index(indexRequest);
        }
        catch (IOException ex){
            logger.warn(ex.getMessage(), ex);
        }
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
        IndexRequest indexRequest = new IndexRequest(notificationIndex, defaultType, idId)
                .source("{}", XContentType.JSON);
        try {
            return searchClient.index(indexRequest).getVersion();
        }
        catch (IOException ex){
            logger.warn(ex.getMessage(), ex);
        }
        return -1;
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
