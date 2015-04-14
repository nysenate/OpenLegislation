package gov.nysenate.openleg.dao.calendar.alert;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCalendarAlertFileQuery implements BasicSqlQuery {

    INSERT_CALENDAR_ALERT_FILE(
            "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR_FILE + "\n" +
            "(file_name, processed_date_time, processed_count," +
            "pending_processing, archived)" + "\n" +
            "VALUES (:fileName, :processedDateTime, :processedCount, " +
            ":pendingProcessing, :archived)"
    ),
    UPDATE_CALENDAR_ALERT_FILE(
            "UPDATE ${schema}." + SqlTable.ALERT_CALENDAR_FILE + "\n" +
            "SET processed_date_time = :processedDateTime," +
            "    processed_count = :processedCount," +
            "    pending_processing = :pendingProcessing," +
            "    archived = :archived " +
            "WHERE file_name = :fileName"
    ),
    GET_PENDING_CALENDAR_ALERT_FILES(
            "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR_FILE + "\n" +
            "WHERE pending_processing = true"
    );

    private String sql;

    SqlCalendarAlertFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
