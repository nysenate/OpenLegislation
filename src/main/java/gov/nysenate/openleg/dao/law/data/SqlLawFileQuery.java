package gov.nysenate.openleg.dao.law.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLawFileQuery implements BasicSqlQuery
{
    GET_PENDING_LAW_FILES(
        "SELECT * FROM ${schema}." + SqlTable.LAW_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_LAW_FILE(
        "INSERT INTO ${schema}." + SqlTable.LAW_FILE + "\n" +
        "(file_name, published_date_time, processed_date_time, processed_count, pending_processing, archived)\n" +
        "VALUES (:fileName, :publishedDateTime, :processedDateTime, :processedCount, :pendingProcessing, :archived)"
    ),
    UPDATE_LAW_FILE(
        "UPDATE ${schema}." + SqlTable.LAW_FILE + "\n" +
        "SET published_date_time = :publishedDateTime, processed_date_time = :processedDateTime, " +
        "    processed_count = :processedCount, pending_processing = :pendingProcessing, archived = :archived\n" +
        "WHERE file_name = :fileName"
    )
    ;

    private String sql;

    SqlLawFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}