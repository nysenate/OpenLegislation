package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.TRANSCRIPT_FILE;

public enum SqlTranscriptFileQuery implements BasicSqlQuery
{
    GET_PENDING_TRANSCRIPT_FILES (
        "SELECT * FROM ${schema}." + TRANSCRIPT_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_TRANSCRIPT_FILE (
        "INSERT INTO ${schema}." + TRANSCRIPT_FILE + "\n" +
        "(file_name, processed_date_time, processed_count, " +
        "pending_processing, archived, date_time, original_filename)" + "\n" +
        "VALUES (:fileName, :processedDateTime, :processedCount, " +
        ":pendingProcessing, :archived, :dateTime, :originalFilename)"
    ),
    UPDATE_TRANSCRIPT_FILE (
        "UPDATE ${schema}." + TRANSCRIPT_FILE + "\n" +
        "SET processed_date_time = :processedDateTime," +
        "    processed_count = :processedCount," +
        "    pending_processing = :pendingProcessing," +
        "    archived = :archived," +
        "    date_time = :dateTime," +
        "    original_filename = :originalFilename " +
        "WHERE file_name = :fileName"
    ),
    OLD_FILE_COUNT (
        "SELECT COUNT(file_name) FROM ${schema}." + TRANSCRIPT_FILE + "\n" +
        "WHERE date_time = :dateTime"
    ),
    GET_BY_FILENAME (
            "SELECT * FROM ${schema}." + TRANSCRIPT_FILE + "\n" +
            "WHERE file_name = :fileName"
    ),
    DELETE_BY_FILENAME (
            "DELETE FROM ${schema}." + TRANSCRIPT_FILE + "\n" +
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
