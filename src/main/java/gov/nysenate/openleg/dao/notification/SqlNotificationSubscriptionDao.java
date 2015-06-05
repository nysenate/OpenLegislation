package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.notification.NotificationDigestSubscription;
import gov.nysenate.openleg.model.notification.NotificationSubscription;
import gov.nysenate.openleg.model.notification.NotificationTarget;
import gov.nysenate.openleg.model.notification.NotificationType;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    /** {@inheritDoc} */
    @Override
    public void removeSubscription(NotificationSubscription subscription) {
        MapSqlParameterSource params = getSubscriptionParams(subscription);
        jdbcNamed.update(DELETE_SUBSCRIPTION.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public Set<NotificationDigestSubscription> getPendingDigests() {
        return new HashSet<>(
                jdbcNamed.query(SELECT_PENDING_DIGESTS.getSql(schema()),
                        new MapSqlParameterSource(), digestSubscriptionRowMapper)
        );
    }

    /** {@inheritDoc} */
    @Override
    public Set<NotificationDigestSubscription> getDigestSubsForUser(String username) {
        return new HashSet<>(
                jdbcNamed.query(SELECT_DIGEST_SUBS_FOR_USER.getSql(schema()),
                        new MapSqlParameterSource("user", username), digestSubscriptionRowMapper)
        );
    }

    /** {@inheritDoc} */
    @Override
    public void insertDigestSubscription(NotificationDigestSubscription subscription) {
        KeyHolder idHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_DIGEST_SUB.getSql(schema()), getDigestSubscriptionParams(subscription),
                idHolder, new String[]{"id"});
        subscription.setId(idHolder.getKey().intValue());
    }

    /** {@inheritDoc} */
    @Override
    public void updateNextDigest(int digestSubscriptionId, LocalDateTime nextDigest) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", digestSubscriptionId)
                .addValue("nextDigest", DateUtils.toDate(nextDigest));
        jdbcNamed.update(UPDATE_NEXT_DIGEST.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDigestSubscription(int digestSubscriptionId) {
        jdbcNamed.update(DELETE_DIGEST_SUB.getSql(schema()), new MapSqlParameterSource("id", digestSubscriptionId));
    }

    private static RowMapper<NotificationSubscription> subscriptionRowMapper = (rs, rowNum) ->
            new NotificationSubscription(
                    rs.getString("user_name"),
                    NotificationType.valueOf(rs.getString("type")),
                    NotificationTarget.valueOf(rs.getString("target")),
                    rs.getString("address")
            );

    private static RowMapper<NotificationDigestSubscription> digestSubscriptionRowMapper = (rs, rowNum) ->
            new NotificationDigestSubscription(
                    subscriptionRowMapper.mapRow(rs, rowNum),
                    rs.getInt("id"),
                    getDurationFromRs(rs, "period"),
                    getPeriodFromRs(rs, "period"),
                    getLocalDateTimeFromRs(rs, "next_digest"),
                    rs.getBoolean("send_empty_digest"),
                    rs.getBoolean("full")
            );

    private MapSqlParameterSource getSubscriptionParams(NotificationSubscription subscription) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user", subscription.getUserName());
        params.addValue("type", subscription.getType().toString());
        params.addValue("target", subscription.getTarget().toString());
        params.addValue("address", subscription.getTargetAddress());
        return params;
    }

    private MapSqlParameterSource getDigestSubscriptionParams(NotificationDigestSubscription subscription) {
        return new MapSqlParameterSource()
                .addValue("id", subscription.getId())
                .addValue("user", subscription.getUserName())
                .addValue("type", subscription.getType().toString())
                .addValue("target", subscription.getTarget().toString())
                .addValue("address", subscription.getTargetAddress())
                .addValue("nextDigest", DateUtils.toDate(subscription.getNextDigest()))
                .addValue("period", DateUtils.toInterval(subscription.getPeriodDays(), subscription.getPeriodHours()))
                .addValue("sendEmptyDigest", subscription.isSendEmptyDigest())
                .addValue("full", subscription.isFull());
    }
}
