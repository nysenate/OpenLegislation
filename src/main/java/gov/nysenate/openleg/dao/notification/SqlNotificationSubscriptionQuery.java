package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlNotificationSubscriptionQuery implements BasicSqlQuery {

    SELECT_ALL_SUBSCRIPTIONS(
        "SELECT * FROM ${schema}." + SqlTable.NOTIFICATION_SUBSCRIPTION
    ),
    INSERT_SUBSCRIPTION(
        "INSERT INTO ${schema}." + SqlTable.NOTIFICATION_SUBSCRIPTION + "\n" +
        "       ( user_name, type,  target,  address)\n" +
        "VALUES (:user,     :type, :target, :address)"
    ),
    DELETE_SUBSCRIPTION(
        "DELETE FROM ${schema}." + SqlTable.NOTIFICATION_SUBSCRIPTION + "\n" +
        "WHERE user_name = :user AND type = :type AND target = :target AND address = :address"
    ),
    SELECT_PENDING_DIGESTS(
        "SELECT * FROM ${schema}." + SqlTable.NOTIFICATION_DIGEST_SUBSCRIPTION + "\n" +
        "WHERE next_digest < now()"
    ),
    SELECT_DIGEST_SUBS_FOR_USER(
        "SELECT * FROM ${schema}." + SqlTable.NOTIFICATION_DIGEST_SUBSCRIPTION + "\n" +
        "WHERE user_name = :username"
    ),
    INSERT_DIGEST_SUB(
        "INSERT INTO ${schema}." + SqlTable.NOTIFICATION_DIGEST_SUBSCRIPTION + "\n" +
        "       (user_name, type, target, address, period, next_digest, send_empty_digest, full)\n" +
        "VALUES(:user, :type, :target, :address, :period, :nextDigest, :sendEmptyDigest,  :full)"
    ),
    UPDATE_NEXT_DIGEST(
        "UPDATE ${schema}." + SqlTable.NOTIFICATION_DIGEST_SUBSCRIPTION + "\n" +
        "SET next_digest = :nextDigest\n" +
        "WHERE id = :id"
    ),
    DELETE_DIGEST_SUB(
        "DELETE FROM ${schema}." + SqlTable.NOTIFICATION_DIGEST_SUBSCRIPTION + "\n" +
        "WHERE id = :id"
    )
    ;

    private String sql;

    private SqlNotificationSubscriptionQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
