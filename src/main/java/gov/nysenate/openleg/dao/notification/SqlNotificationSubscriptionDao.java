package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.notification.*;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.notification.SqlNotificationSubscriptionQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;
import static gov.nysenate.openleg.util.DateUtils.toTime;

@Repository
public class SqlNotificationSubscriptionDao extends SqlBaseDao implements NotificationSubscriptionDao {

    /** {@inheritDoc} */
    @Override
    public Set<NotificationSubscription> getSubscriptions() {
        return new HashSet<>(
                jdbcNamed.query(SELECT_ALL_SUBSCRIPTIONS.getSql(schema()),
                    new MapSqlParameterSource(), notificationSubscriptionRowMapper)
        );
    }

    @Override
    public NotificationSubscription getSubscription(int subscriptionId) throws SubscriptionNotFoundEx {
        MapSqlParameterSource params = getSubscriptionIdParams(subscriptionId);
        try {
            return jdbcNamed.queryForObject(
                    SELECT_SUBSCRIPTION_BY_ID.getSql(schema()),
                    params,
                    notificationSubscriptionRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new SubscriptionNotFoundEx(subscriptionId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public NotificationSubscription updateSubscription(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionParams(subscription);
        Integer subId = subscription.getId();
        // Insert if there is no valid id, or an update attempt affects no rows
        if (subId == null || jdbcNamed.update(UPDATE_SUBSCRIPTION.getSql(schema()), params) == 0) {
            KeyHolder subIdHolder = new GeneratedKeyHolder();
            jdbcNamed.update(INSERT_SUBSCRIPTION.getSql(schema()), params, subIdHolder, new String[]{"id"});
            subId = Objects.requireNonNull(subIdHolder.getKey()).intValue();
            // Replace the subscription parameter with an id'd version
            subscription = subscription.copy().setId(subId).build();
        }

        if (subscription instanceof ScheduledNotificationSubscription) {
            updateScheduledSubscription((ScheduledNotificationSubscription) subscription);
        } else if (subscription instanceof InstantNotificationSubscription) {
            updateInstantSubscription((InstantNotificationSubscription) subscription);
        } else {
            throw new IllegalArgumentException(
                    "Unknown notification subscription subclass: " + subscription.getClass());
        }
        return subscription;
    }

    /** {@inheritDoc} */
    @Override
    public void removeSubscription(int subscriptionId) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscriptionId);
        jdbcNamed.update(DELETE_SUBSCRIPTION.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void setLastSent(int subscriptionId, LocalDateTime lastSentDateTime) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscriptionId);
        params.addValue("lastSent", toDate(lastSentDateTime));
        jdbcNamed.update(UPDATE_SUBSCRIPTION_LAST_SENT.getSql(schema()), params);
    }

    /* --- Internal Methods --- */

    /** Updates fields unique to {@link ScheduledNotificationSubscription} */
    private void updateScheduledSubscription(ScheduledNotificationSubscription subscription) {
        MapSqlParameterSource params = getScheduledSubParams(subscription);
        if (jdbcNamed.update(UPDATE_SCHEDULE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_SCHEDULE.getSql(schema()), params);
        }
    }

    /** Updates fields unique to {@link InstantNotificationSubscription} */
    private void updateInstantSubscription(InstantNotificationSubscription subscription) {
        MapSqlParameterSource params = getInstantSubParams(subscription);
        if (jdbcNamed.update(UPDATE_RATE_LIMIT.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_RATE_LIMIT.getSql(schema()), params);
        }
    }

    /* --- Row Mappers --- */

    private static void mapSubscriptionFields(ResultSet rs, NotificationSubscription.Builder builder) throws SQLException {
        builder
                .setId(rs.getInt("id"))
                .setUserName(rs.getString("user_name"))
                .setNotificationType(NotificationType.valueOf(rs.getString("notification_type")))
                .setMedium(NotificationMedium.getValue(rs.getString("medium")))
                .setTargetAddress(rs.getString("address"))
                .setLastSent(getLocalDateTimeFromRs(rs, "last_sent"))
                .setDetail(rs.getBoolean("detail"))
                .setActive(rs.getBoolean("active"))
        ;
    }

    private static final RowMapper<ScheduledNotificationSubscription> schedNotifSubRowMapper = (rs, rowNum) -> {
        ScheduledNotificationSubscription.Builder builder = ScheduledNotificationSubscription.builder();
        mapSubscriptionFields(rs, builder);
        List<Short> dayNums = Arrays.asList((Short[]) rs.getArray("days_of_week").getArray());
        List<DayOfWeek> days = dayNums.stream()
                .map(DayOfWeek::of)
                .collect(Collectors.toList());
        builder.setDaysOfWeek(days);
        builder.setTimeOfDay(getLocalTimeFromRs(rs, "time_of_day"));
        builder.setSendEmpty(rs.getBoolean("send_empty"));
        return builder.build();
    };

    private static final RowMapper<InstantNotificationSubscription> instantNotifSubRowMapper = (rs, rowNum) -> {
        InstantNotificationSubscription.Builder builder = InstantNotificationSubscription.builder();
        mapSubscriptionFields(rs, builder);
        builder.setRateLimit(getDurationFromRs(rs, "rate_limit"));
        return builder.build();
    };

    private static final RowMapper<NotificationSubscription> notificationSubscriptionRowMapper = (rs, rowNum) -> {
        String subTypeStr = rs.getString("subscription_type");
        // (subTypeStr shouldn't be null)
        NotificationSubscriptionType subType = NotificationSubscriptionType.valueOf(subTypeStr.toUpperCase());
        // Use the appropriate mapper for the subscription type.
        switch (subType) {
            case INSTANT:
                return instantNotifSubRowMapper.mapRow(rs, rowNum);
            case SCHEDULED:
                return schedNotifSubRowMapper.mapRow(rs, rowNum);
            default:
                throw new IllegalArgumentException("Unrecognized subscription type: " + subType);
        }

    };

    /* --- Parameter Mapping --- */

    private MapSqlParameterSource getSubscriptionIdParams(Integer subscriptionId) {
        return new MapSqlParameterSource("id", subscriptionId);
    }

    private MapSqlParameterSource getSubscriptionParams(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscription.getId());
        params.addValue("user", subscription.getUserName());
        params.addValue("subType", subscription.getSubscriptionType().toString().toLowerCase());
        params.addValue("notifType", subscription.getNotificationType().toString());
        params.addValue("medium", subscription.getMedium().toString());
        params.addValue("address", subscription.getTargetAddress());
        params.addValue("detail", subscription.isDetail());
        params.addValue("active", subscription.isActive());
        params.addValue("lastSent", toDate(subscription.getLastSent()));
        return params;
    }

    private MapSqlParameterSource getInstantSubParams(InstantNotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscription.getId());
        params.addValue("rateLimit", DateUtils.toInterval(Period.ZERO, subscription.getRateLimit()));
        return params;
    }

    private MapSqlParameterSource getScheduledSubParams(ScheduledNotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscription.getId());
        params.addValue("daysOfWeek",
                toPostgresArray(subscription.getDaysOfWeek().stream()
                        .map(DayOfWeek::getValue)
                        .map(Integer::shortValue)
                        .collect(Collectors.toList())));
        params.addValue("timeOfDay", toTime(subscription.getTimeOfDay()));
        params.addValue("sendEmpty", subscription.sendEmpty());
        return params;
    }

}
