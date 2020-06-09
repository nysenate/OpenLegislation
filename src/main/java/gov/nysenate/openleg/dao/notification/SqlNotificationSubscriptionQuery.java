package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;

import static gov.nysenate.openleg.dao.base.SqlTable.*;

public enum SqlNotificationSubscriptionQuery implements BasicSqlQuery {

    SELECT_ALL_SUBSCRIPTIONS("" +
            "SELECT * \n" +
            "FROM ${schema}." + NOTIFICATION_SUBSCRIPTION + " sub\n" +
            "LEFT JOIN ${schema}." + NOTIFICATION_SCHEDULE + " sched\n" +
            "  ON sub.id = sched.subscription_id\n" +
            "LEFT JOIN ${schema}." + NOTIFICATION_RATE_LIMIT + " rl\n" +
            "  ON sub.id = rl.subscription_id"
    ),
    SELECT_SUBSCRIPTION_BY_ID(SELECT_ALL_SUBSCRIPTIONS.sql + "\n" +
            "WHERE sub.id = :id"
    ),
    INSERT_SUBSCRIPTION("" +
            "INSERT INTO ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "  (subscription_type, user_name, notification_type, medium,  address, detail, last_sent, active)\n" +
            "VALUES (:subType::${schema}.notification_subscription_type, :user, :notifType, :medium, :address, :detail, \n" +
            "        :lastSent, :active)"
    ),
    UPDATE_SUBSCRIPTION("" +
            "UPDATE ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "SET subscription_type = :subType::${schema}.notification_subscription_type, user_name = :user,\n" +
            "    notification_type = :notifType, medium = :medium, address = :address, detail = :detail,\n" +
            "    active = :active, last_sent = :lastSent\n" +
            "WHERE id = :id"
    ),
    UPDATE_SUBSCRIPTION_LAST_SENT("" +
            "UPDATE ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "SET last_sent = :lastSent\n" +
            "WHERE id = :id"
    ),
    DELETE_SUBSCRIPTION("" +
            "DELETE FROM ${schema}." + NOTIFICATION_SUBSCRIPTION + "\n" +
            "WHERE id = :id"
    ),
    INSERT_SCHEDULE("" +
            "INSERT INTO ${schema}." + NOTIFICATION_SCHEDULE + "\n" +
            "  (subscription_id, days_of_week,       time_of_day, send_empty)\n" +
            "VALUES (:id, :daysOfWeek::smallint[],  :timeOfDay,  :sendEmpty)"
    ),
    UPDATE_SCHEDULE("" +
            "UPDATE ${schema}." + NOTIFICATION_SCHEDULE + "\n" +
            "SET days_of_week = :daysOfWeek::smallint[],\n" +
            "    time_of_day = :timeOfDay, send_empty = :sendEmpty\n" +
            "WHERE subscription_id = :id"
    ),
    INSERT_RATE_LIMIT("" +
            "INSERT INTO ${schema}." + NOTIFICATION_RATE_LIMIT + "\n" +
            "  (subscription_id, rate_limit)\n" +
            "VALUES (:id,       :rateLimit)"
    ),
    UPDATE_RATE_LIMIT("" +
            "UPDATE ${schema}." + NOTIFICATION_RATE_LIMIT + "\n" +
            "SET rate_limit = :rateLimit\n" +
            "WHERE subscription_id = :id"
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
