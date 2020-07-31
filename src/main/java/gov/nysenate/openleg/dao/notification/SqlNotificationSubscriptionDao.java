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

        return subscription;
    }

    /** {@inheritDoc} */
    @Override
    public void removeSubscription(int subscriptionId) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscriptionId);
        jdbcNamed.update(DELETE_SUBSCRIPTION.getSql(schema()), params);
    }

    /* --- Row Mappers --- */

    private static void mapSubscriptionFields(ResultSet rs, NotificationSubscription.Builder builder) throws SQLException {
        builder
                .setId(rs.getInt("id"))
                .setUserName(rs.getString("user_name"))
                .setNotificationType(NotificationType.valueOf(rs.getString("notification_type")))
                .setMedium(NotificationMedium.getValue(rs.getString("medium")))
                .setTargetAddress(rs.getString("address"))
                .setDetail(rs.getBoolean("detail"))
                .setActive(rs.getBoolean("active"))
        ;
    }

    private static final RowMapper<NotificationSubscription> notificationSubscriptionRowMapper = (rs, rowNum) -> {
        NotificationSubscription sub = new NotificationSubscription.Builder()
                .setId(rs.getInt("id"))
                .setUserName(rs.getString("user_name"))
                .setNotificationType(NotificationType.valueOf(rs.getString("notification_type")))
                .setMedium(NotificationMedium.valueOf(rs.getString("medium")))
                .setTargetAddress(rs.getString("address"))
                .setDetail(rs.getBoolean("detail"))
                .setActive(rs.getBoolean("active"))
                .build();
        return sub;
    };

    /* --- Parameter Mapping --- */

    private MapSqlParameterSource getSubscriptionIdParams(Integer subscriptionId) {
        return new MapSqlParameterSource("id", subscriptionId);
    }

    private MapSqlParameterSource getSubscriptionParams(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionIdParams(subscription.getId());
        params.addValue("user", subscription.getUserName());
        params.addValue("notifType", subscription.getNotificationType().toString());
        params.addValue("medium", subscription.getMedium().toString());
        params.addValue("address", subscription.getTargetAddress());
        params.addValue("detail", subscription.isDetail());
        params.addValue("active", subscription.isActive());
        return params;
    }
}
