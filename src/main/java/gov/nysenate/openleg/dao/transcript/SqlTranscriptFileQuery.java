package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlTranscriptFileQuery implements BasicSqlQuery
{
    GET_PENDING_TRANSCRIPT_FILES(
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_TRANSCRIPT_FILE(
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT_FILE + "\n" +
        "(file_name, processed_date_time, processed_count," +
        "pending_processing, archived)" + "\n" +
        "VALUES (:fileName, :processedDateTime, :processedCount, " +
        ":pendingProcessing, :archived)"
    ),
    UPDATE_TRANSCRIPT_FILE(
        "UPDATE ${schema}." + SqlTable.TRANSCRIPT_FILE + "\n" +
        "SET processed_date_time = :processedDateTime," +
        "    processed_count = :processedCount," +
        "    pending_processing = :pendingProcessing," +
        "    archived = :archived " +
        "WHERE file_name = :fileName"
    );

    private String sql;

    SqlTranscriptFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
