package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.NotificationType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.openleg.dao.notification.SqlNotificationSubscriptionQuery.*;

@Repository
public class SqlNotificationSubscriptionDao extends SqlBaseDao implements NotificationSubscriptionDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<NotificationSubscription> getSubscriptions() {
        return new HashSet<>(
                jdbcNamed.query(SELECT_ALL_SUBSCRIPTIONS.getSql(schema()),
                    new MapSqlParameterSource(), subscriptionRowMapper)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSubscription(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionParams(subscription);
        jdbcNamed.update(INSERT_SUBSCRIPTION.getSql(schema()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSubscription(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionParams(subscription);
        jdbcNamed.update(DELETE_SUBSCRIPTION.getSql(schema()), params);
    }

    private static RowMapper<NotificationSubscription> subscriptionRowMapper =
            (rs, rowNum) -> new NotificationSubscription(
                    rs.getString("user_name"),
                    NotificationType.valueOf(rs.getString("type")),
                    NotificationTarget.valueOf(rs.getString("target")),
                    rs.getString("address")
            );

    private MapSqlParameterSource getSubscriptionParams(NotificationSubscription subscription) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user", subscription.getUserName());
        params.addValue("type", subscription.getType().toString());
        params.addValue("target", subscription.getTarget().toString());
        params.addValue("address", subscription.getTargetAddress());
        return params;
    }
}
