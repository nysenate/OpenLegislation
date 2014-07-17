package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SqlQueryEnum;
import gov.nysenate.openleg.dao.base.SqlQueryUtils;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlSobiFileQuery implements SqlQueryEnum
{
    GET_SOBI_FILES_BY_FILE_NAMES_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "WHERE file_name IN (:fileNames)"
    ),
    GET_SOBI_FILES_DURING_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "WHERE (published_date_time BETWEEN :startDate AND :endDate) AND (:processedOnly = false OR processed_count > 0)"
    ),
    GET_PENDING_SOBI_FILES_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_SOBI_FILE(
        "INSERT INTO ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "(file_name, published_date_time, staged_date_time, processed_date_time, processed_count, pending_processing) " +
        "VALUES (:fileName, :publishedDateTime, :stagedDateTime, :processedDateTime, :processedCount, :pendingProcessing)"
    ),
    UPDATE_SOBI_FILE(
        "UPDATE ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "SET published_date_time = :publishedDateTime," +
        "    staged_date_time = :stagedDateTime," +
        "    processed_date_time = :processedDateTime," +
        "    pending_processing = :pendingProcessing," +
        "    processed_count = :processedCount " +
        "WHERE file_name = :fileName"
    );

    private String sql;

    SqlSobiFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String environmentSchema) {
        return SqlQueryUtils.getSqlWithSchema(this.sql, environmentSchema);
    }
}
