package gov.nysenate.openleg.search.notifications;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.notification.view.NotificationView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class ElasticNotificationSearchDao extends ElasticBaseDao<NotificationView>
        implements NotificationSearchDao, IndexedSearchService<RegisteredNotification> {
    private SynchronizedLong nextId;

    @Autowired
    public ElasticNotificationSearchDao(EventBus eventBus) {
        eventBus.register(this);
    }

    @PostConstruct
    private void init() throws IOException {
        this.nextId = new SynchronizedLong(getDocCount() + 1);
    }

    /* --- Implemented Methods --- */

    @Override
    public Optional<RegisteredNotification> getNotification(long notificationId) {
        Optional<NotificationView> view = getRequest(Long.toString(notificationId));
        return view.map(ElasticNotificationSearchDao::viewToRegNotification);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> searchNotifications(Query query,
                                                                     List<SortOptions> sort, LimitOffset limitOffset) {
        return search(query, null,
                sort, limitOffset, true, ElasticNotificationSearchDao::viewToRegNotification);
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        var regNotification = new RegisteredNotification(notification, nextId.getAndIncrement());
        indexDoc(String.valueOf(regNotification.getId()), new NotificationView(regNotification));
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

    /**
     * A class to ensure two Notification IDs never conflict.
     */
    private static class SynchronizedLong {
        private long num;

        public SynchronizedLong(long num) {
            this.num = num;
        }

        public synchronized long getAndIncrement() {
            return num++;
        }
    }

    private static RegisteredNotification viewToRegNotification(NotificationView view) {
        return new RegisteredNotification(view.getId(), view.getNotificationType(),
                view.getOccurred(), view.getSummary(), view.getMessage());
    }
}
