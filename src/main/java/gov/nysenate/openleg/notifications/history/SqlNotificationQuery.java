package gov.nysenate.openleg.notifications.history;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.NOTIFICATION;

public enum SqlNotificationQuery implements BasicSqlQuery {

    SELECT_NOTIFICATION_BY_ID(
            "SELECT *\n" +
            "FROM ${schema}." + NOTIFICATION + "\n" +
            "WHERE id = :id"
    ),
    SELECT_NOTIFICATIONS(
            "SELECT *, COUNT(*) OVER () AS total_count\n" +
            "FROM ${schema}." + NOTIFICATION + "\n" +
            "WHERE type IN (:types)\n" +
            "  AND occurred BETWEEN :startDateTime AND :endDateTime"
    ),
    INSERT_NOTIFICATION(
            "INSERT INTO ${schema}." + NOTIFICATION + " (type, occurred, summary, message)\n" +
            "VALUES (:type, :occurred, :summary, :message)"
    );

    private final String sql;

    SqlNotificationQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
