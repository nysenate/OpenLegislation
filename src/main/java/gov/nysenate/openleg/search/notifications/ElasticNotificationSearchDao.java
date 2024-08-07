package gov.nysenate.openleg.search.notifications;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.api.notification.view.NotificationView;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

@Repository
public class ElasticNotificationSearchDao extends ElasticBaseDao<Long, NotificationView, RegisteredNotification> {
    private SynchronizedLong nextId;

    @Autowired
    public ElasticNotificationSearchDao(EventBus eventBus) {
        eventBus.register(this);
    }

    @PostConstruct
    private void init() throws IOException {
        this.nextId = new SynchronizedLong(getDocCount() + 1);
    }

    public Optional<RegisteredNotification> getNotification(long notificationId) {
        Optional<NotificationView> view = getRequest(notificationId);
        return view.map(ElasticNotificationSearchDao::viewToRegNotification);
    }

    public RegisteredNotification registerNotification(Notification notification) {
        var regNotification = new RegisteredNotification(notification, nextId.getAndIncrement());
        updateIndex(regNotification);
        return regNotification;
    }

    /** {@inheritDoc} */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.NOTIFICATION;
    }

    @Override
    protected Long getId(RegisteredNotification data) {
        return data.getId();
    }

    @Override
    protected NotificationView getDoc(RegisteredNotification data) {
        return new NotificationView(data);
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
