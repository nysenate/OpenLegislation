package gov.nysenate.openleg.service.notification.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.notification.NotificationSearchDao;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ElasticNotificationService implements NotificationService {

    @Autowired protected NotificationSearchDao notificationDao;

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
    public PaginatedList<RegisteredNotification> getNotificationList(Set<NotificationType> types, Range<LocalDateTime> dateTimeRange,
                                                                     SortOrder order, LimitOffset limitOffset) {
        ArrayList<FilterBuilder> filters = new ArrayList<>();
        if (dateTimeRange != null && !dateTimeRange.encloses(DateUtils.ALL_DATE_TIMES)) {
            filters.add(FilterBuilders.rangeFilter("occurred")
                        .from(DateUtils.startOfDateTimeRange(dateTimeRange))
                        .to(DateUtils.endOfDateTimeRange(dateTimeRange)));
        }
        if (types != null && !types.contains(NotificationType.ALL)) {
            Set<NotificationType> coveredTypes = types.stream()
                    .map(NotificationType::getCoverage)
                    .reduce(new HashSet<>(), (a, b) -> {a.addAll(b); return a;});
            filters.add(FilterBuilders.termsFilter("type", coveredTypes));
        }
        FilterBuilder filter = filters.size() > 0
                ? FilterBuilders.andFilter(filters.toArray(new FilterBuilder[filters.size()]))
                : FilterBuilders.matchAllFilter();

        String sort = String.format("occurred:%s", order != null ? order.toString() : "DESC");

        return notificationDao.searchNotifications(QueryBuilders.matchAllQuery(), filter, sort, limitOffset)
                .toPaginatedList();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> notificationSearch(String queryString, String sort, LimitOffset limitOffset) {
        return notificationDao.searchNotifications(QueryBuilders.queryString(queryString),
                                                    FilterBuilders.matchAllFilter(), sort, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        try {
            return notificationDao.registerNotification(notification);
        } catch (ElasticsearchException ex) {
            return new RegisteredNotification(notification, -1);
        }
    }
}
