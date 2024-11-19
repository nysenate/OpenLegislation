package gov.nysenate.openleg.search.notifications;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import gov.nysenate.openleg.api.notification.view.NotificationView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import gov.nysenate.openleg.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ElasticNotificationService extends IndexedSearchService<RegisteredNotification>
        implements NotificationService {
    private final ElasticNotificationSearchDao notificationDao;

    @Autowired
    public ElasticNotificationService(ElasticNotificationSearchDao notificationDao) {
        super(notificationDao);
        this.notificationDao = notificationDao;
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification getNotification(long notificationId) throws NotificationNotFoundException {
        Optional<RegisteredNotification> notificationOptional = notificationDao.getNotification(notificationId);
        if (notificationOptional.isPresent()) {
            return notificationOptional.get();
        }
        throw new NotificationNotFoundException(notificationId);
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<NotificationView> getNotificationList(
            Set<NotificationType> types, LocalDateTime from, LocalDateTime to,
            SortOrder order, LimitOffset limitOffset) throws SearchException {
        var filterQuery = new BoolQuery.Builder();

        if (!types.equals(EnumSet.allOf(NotificationType.class))) {
            // Convert to lowercase strings for term query.
            List<FieldValue> typeValues = types.stream()
                    .map(Enum::name).map(String::toLowerCase).map(FieldValue::of).toList();
            filterQuery.filter(TermsQuery.of(b -> b.field("notificationType").terms(
                    TermsQueryField.of(tqfb -> tqfb.value(typeValues))
            ))._toQuery());
        }

        if (from != null || to != null) {
            var rangeQuery = new RangeQuery.Builder().field("occurred");
            if (from != null) {
                rangeQuery.from(from.toString());
            }
            if (to != null) {
                rangeQuery.to(to.toString());
            }
            filterQuery.filter(rangeQuery.build()._toQuery());
        }

        String sortString = (order == null || order == SortOrder.NONE)
                ? "" : "occurred:" + order;

        SearchResults<NotificationView> results = notificationDao.searchForDocs(
                filterQuery.build(), sortString, limitOffset);
        return results.toPaginatedList();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<NotificationView> notificationSearch(String queryStr, String sort, LimitOffset limitOffset) throws SearchException {
        return notificationDao.searchForDocs(
                ElasticSearchServiceUtils.getStringQuery(queryStr), sort, limitOffset
        );
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        return notificationDao.registerNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {}
}
