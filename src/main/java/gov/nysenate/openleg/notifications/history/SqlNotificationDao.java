package gov.nysenate.openleg.notifications.history;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.notifications.model.RegisteredNotification;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static gov.nysenate.openleg.notifications.history.SqlNotificationQuery.*;

@Repository
public class SqlNotificationDao extends SqlBaseDao implements NotificationDao {

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification getNotification(long notificationId) throws NotificationNotFoundException {
        try {
            return jdbcNamed.queryForObject(SELECT_NOTIFICATION_BY_ID.getSql(schema()),
                    new MapSqlParameterSource("id", notificationId), notificationRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotificationNotFoundException(notificationId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<RegisteredNotification> getNotifications(Set<NotificationType> types,
                                                                  Range<LocalDateTime> dateTimeRange,
                                                                  SortOrder order, LimitOffset limitOffset) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange)
                .addValue("types", types.stream().map(Enum::name).toList());
        OrderBy orderBy = new OrderBy("occurred", order, "id", order);
        var handler = new PaginatedRowHandler<>(limitOffset, "total_count", notificationRowMapper);
        jdbcNamed.query(SELECT_NOTIFICATIONS.getSql(schema(), orderBy, limitOffset), params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public RegisteredNotification registerNotification(Notification notification) {
        var params = new MapSqlParameterSource()
                .addValue("type", notification.getNotificationType().name())
                .addValue("occurred", DateUtils.toDate(notification.getOccurred()))
                .addValue("summary", notification.getSummary())
                .addValue("message", notification.getMessage());
        KeyHolder idHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_NOTIFICATION.getSql(schema()), params, idHolder, new String[]{"id"});
        return new RegisteredNotification(notification, Objects.requireNonNull(idHolder.getKey()).longValue());
    }

    private static final RowMapper<RegisteredNotification> notificationRowMapper = (rs, rowNum) ->
            new RegisteredNotification(rs.getLong("id"), NotificationType.valueOf(rs.getString("type")),
                    getLocalDateTimeFromRs(rs, "occurred"), rs.getString("summary"), rs.getString("message"));
}
