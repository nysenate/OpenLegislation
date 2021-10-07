package gov.nysenate.openleg.notifications.subscription;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.*;

public enum SqlNotificationSubscriptionQuery implements BasicSqlQuery {

    SELECT_ALL_SUBSCRIPTIONS("" +
            "SELECT * \n" +
            "FROM ${schema}." + NOTIFICATION_SUBSCRIPTION + " sub"
    ),
    SELECT_SUBSCRIPTION_BY_ID(SELECT_ALL_SUBSCRIPTIONS.sql + "\n" +
            "WHERE sub.id = :id"
    ),
    INSERT_SUBSCRIPTION("" +
            "INSERT INTO ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "  (user_name, notification_type, medium,  address, detail, active)\n" +
            "VALUES (:user, :notifType, :medium, :address, :detail, :active)"
    ),
    UPDATE_SUBSCRIPTION("" +
            "UPDATE ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "SET user_name = :user, notification_type = :notifType, medium = :medium, \n" +
            "    address = :address, detail = :detail,\n" +
            "    active = :active, last_sent = :lastSent\n" +
            "WHERE id = :id"
    ),
    DELETE_SUBSCRIPTION("" +
            "DELETE FROM ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "WHERE id = :id"
    ),
    ;

    private String sql;

    SqlNotificationSubscriptionQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
