package gov.nysenate.openleg.dao.notification;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.notification.RegisteredNotification;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.DateUtils;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.notification.SqlNotificationQuery.*;

@Repository
public class SqlNotificationDao extends SqlBaseDao implements NotificationDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlNotificationDao.class);

    /**
     * {@inheritDoc}
     */
//    @Override
    public RegisteredNotification getNotification(int notificationId) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", notificationId);
        return jdbcNamed.queryForObject(SELECT_NOTIFICATION_BY_ID.getSql(schema()), params, notificationRowMapper);
    }

    /**
     * {@inheritDoc}
     */
//    @Override
    public PaginatedList<RegisteredNotification> getNotifications(Collection<NotificationType> types,
                                                        Range<LocalDateTime> dateTimeRange,
                                                        SortOrder order, LimitOffset limitOffset) {
        MapSqlParameterSource params = getNotificationQueryParams(types, dateTimeRange);
        PaginatedRowHandler<RegisteredNotification> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total", notificationRowMapper);
        jdbcNamed.query(SELECT_NOTIFICATIONS.getSql(schema(), new OrderBy("occurred", order), limitOffset),
                        params, rowHandler);
        return rowHandler.getList();
    }

    @Override
    public SearchResults<RegisteredNotification> searchNotifications(QueryBuilder query, FilterBuilder filter, String sort, LimitOffset limitOffset) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        try {
            MapSqlParameterSource params = getNotificationBodyParams(notification);
            KeyHolder idHolder = new GeneratedKeyHolder();
            jdbcNamed.update(INSERT_NOTIFICATION.getSql(schema()), params, idHolder, new String[]{"id"});
            return new RegisteredNotification(notification, idHolder.getKey().intValue());
        } catch (DataAccessException ex) {
            return new RegisteredNotification(notification, -1);
        }
    }

    private static RowMapper<RegisteredNotification> notificationRowMapper =
            (rs, rowNum) -> new RegisteredNotification(
                    rs.getInt("id"),
                    NotificationType.getValue(rs.getString("type")),
                    getLocalDateTimeFromRs(rs, "occurred"),
                    rs.getString("summary"),
                    rs.getString("message")
                );

    MapSqlParameterSource getNotificationQueryParams(Collection<NotificationType> types,
                                                     Range<LocalDateTime> dateTimeRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        params.addValue("types", types.stream().map(NotificationType::toString).collect(Collectors.toList()));
        return params;
    }

    MapSqlParameterSource getNotificationBodyParams(Notification notification) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", notification.getType().toString());
        params.addValue("occurred", DateUtils.toDate(notification.getOccurred()));
        params.addValue("summary", notification.getSummary());
        params.addValue("message", notification.getMessage());
        return params;
    }

}
