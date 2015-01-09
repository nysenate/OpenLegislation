package gov.nysenate.openleg.dao.notification;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlNotificationQuery implements BasicSqlQuery {

    SELECT_NOTIFICATION_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.NOTIFICATION + "\n" +
        "WHERE id = :id"
    ),
    SELECT_NOTIFICATIONS(
        "SELECT id, type, occurred, summary, message, COUNT(*) OVER() AS total FROM ${schema}." + SqlTable.NOTIFICATION + "\n" +
        "WHERE type IN (:types) AND occurred BETWEEN :startDateTime AND :endDateTime"
    ),
    INSERT_NOTIFICATION(
        "INSERT INTO ${schema}." + SqlTable.NOTIFICATION + "\n" +
        "       ( type,  occurred,  summary,  message)\n" +
        "VALUES (:type, :occurred, :summary, :message)"
    )
    ;

    private String sql;

    private SqlNotificationQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
