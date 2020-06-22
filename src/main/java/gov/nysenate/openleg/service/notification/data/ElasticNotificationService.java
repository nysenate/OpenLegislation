package gov.nysenate.openleg.service.notification.data;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.notification.NotificationSearchDao;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.model.search.UnexpectedSearchException;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ElasticNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticNotificationService.class);

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
    public PaginatedList<RegisteredNotification> getNotificationList(Set<NotificationType> types,
                                                                     Range<LocalDateTime> dateTimeRange,
                                                                     SortOrder order,
                                                                     LimitOffset limitOffset) throws SearchException {
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

        if (!types.equals(EnumSet.allOf(NotificationType.class))) {
            // Convert to lowercase strings for term query.
            List<String> typeValues = types.stream()
                    .map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
            filterQuery.must(QueryBuilders.termsQuery("notificationType", typeValues));
        }

        if (dateTimeRange.hasUpperBound() || dateTimeRange.hasLowerBound()) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("occurred");
            if (dateTimeRange.hasLowerBound()) {
                rangeQuery.from(dateTimeRange.lowerEndpoint(),
                        dateTimeRange.lowerBoundType() == BoundType.CLOSED);
            }
            if (dateTimeRange.hasUpperBound()) {
                rangeQuery.to(dateTimeRange.upperEndpoint(),
                        dateTimeRange.upperBoundType() == BoundType.CLOSED);
            }
            filterQuery.must(rangeQuery);
        }

        String sortString = (order != null && order != SortOrder.NONE)
                ? "occurred:" + order
                : "";

        try {
            SearchResults<RegisteredNotification> results = notificationDao.searchNotifications(
                    QueryBuilders.matchAllQuery(), filterQuery,
                    ElasticSearchServiceUtils.extractSortBuilders(sortString), limitOffset);
            return results.toPaginatedList();
        } catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> notificationSearch(String queryString, String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        try {
            return notificationDao.searchNotifications(QueryBuilders.queryStringQuery(queryString), QueryBuilders.matchAllQuery(),
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
        } catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        try {
            return notificationDao.registerNotification(notification);
        } catch (ElasticsearchException ex) {
            logger.error("Failed to register notification!", ex);
            return new RegisteredNotification(notification, -1);
        }
    }
}
