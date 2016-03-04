package gov.nysenate.openleg.service.notification.data;

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
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    public PaginatedList<RegisteredNotification> getNotificationList(Set<NotificationType> types, Range<LocalDateTime> dateTimeRange,
                                                                     SortOrder order, LimitOffset limitOffset) throws SearchException {
        //Todo figure out why the notificationType term filter doesn't work
//        FilterBuilder rangeFilter = FilterBuilders.matchAllFilter();
//        FilterBuilder typesFilter = FilterBuilders.matchAllFilter();
//        if (dateTimeRange != null && !dateTimeRange.encloses(DateUtils.ALL_DATE_TIMES)) {
//            rangeFilter = FilterBuilders.rangeFilter("occurred")
//                        .from(DateUtils.startOfDateTimeRange(dateTimeRange))
//                        .to(DateUtils.endOfDateTimeRange(dateTimeRange));
//        }
//        if (types != null && !types.isEmpty() && !types.contains(NotificationType.ALL)) {
//            List<String> coveredTypes = types.stream()
//                    .map(NotificationType::getCoverage)
//                    .flatMap(Set::stream)
//                    .map(NotificationType::toString)
//                    .collect(Collectors.toList());
//            logger.info("{}", coveredTypes);
//            typesFilter = FilterBuilders.termsFilter("notificationType", coveredTypes);
//        }
//        FilterBuilder filter = FilterBuilders.andFilter(rangeFilter, typesFilter);
//
//        String sort = String.format("occurred:%s", order != null ? order.toString() : "DESC");
//
//        return notificationDao.searchNotifications(QueryBuilders.matchAllQuery(), filter, sort, limitOffset)
//                .toPaginatedList();
        StringBuilder queryStringBuilder = new StringBuilder();
        List<String> coveredTypes = types.stream()
                .map(NotificationType::getCoverage)
                .flatMap(Set::stream)
                .map(NotificationType::toString)
                .collect(Collectors.toList());
        if (!coveredTypes.isEmpty()) {
            queryStringBuilder.append("notificationType:");
            if (coveredTypes.size() > 1) {
                queryStringBuilder.append("(");
            }
            boolean firstType = true;
            for (String type : coveredTypes) {
                if (firstType) {
                    firstType = false;
                } else {
                    queryStringBuilder.append(" ");
                }
                queryStringBuilder.append(type);
            }
            if (coveredTypes.size() > 1) {
                queryStringBuilder.append(")");
            }
            queryStringBuilder.append(" AND ");
        }
        queryStringBuilder.append("occurred:[")
                .append(DateUtils.startOfDateTimeRange(dateTimeRange))
                .append(" TO ")
                .append(DateUtils.endOfDateTimeRange(dateTimeRange))
                .append("]");
        String sortString = "occurred:" + order;
        return notificationSearch(queryStringBuilder.toString(), sortString, limitOffset).toPaginatedList();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<RegisteredNotification> notificationSearch(String queryString, String sort, LimitOffset limitOffset) throws SearchException {
        if (limitOffset == null) {
            limitOffset = LimitOffset.ALL;
        }
        try {
            return notificationDao.searchNotifications(QueryBuilders.queryString(queryString), FilterBuilders.matchAllFilter(),
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limitOffset);
        } catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex);
        }
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
