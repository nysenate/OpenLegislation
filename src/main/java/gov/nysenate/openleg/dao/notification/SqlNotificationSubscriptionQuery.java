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
