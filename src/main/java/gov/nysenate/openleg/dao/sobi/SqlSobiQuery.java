package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlSobiQuery implements BasicSqlQuery
{
    /** --- Sobi Files --- */

    GET_SOBI_FILES_BY_FILE_NAMES(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "WHERE file_name IN (:fileNames)"
    ),
    GET_SOBI_FILES_DURING(
        "SELECT file_name, published_date_time, staged_date_time, encoding, archived, COUNT(*) OVER () AS total_count\n" +
        "FROM ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "WHERE (published_date_time BETWEEN :startDate AND :endDate)"
    ),
    INSERT_SOBI_FILE(
        "INSERT INTO ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "(file_name, published_date_time, encoding, archived) " +
        "VALUES (:fileName, :publishedDateTime, :encoding, :archived)"
    ),
    UPDATE_SOBI_FILE(
        "UPDATE ${schema}." + SqlTable.SOBI_FILE + "\n" +
        "SET published_date_time = :publishedDateTime," +
        "    encoding = :encoding," +
        "    archived = :archived " +
        "WHERE file_name = :fileName"
    ),

    /** --- Sobi Fragments --- */

    CHECK_SOBI_FRAGMENT_EXISTS(
        "SELECT 1 FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    GET_SOBI_FRAGMENT_BY_FILE_NAME(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    GET_SOBI_FRAGMENTS_BY_SOBI_FILE(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE sobi_file_name = :sobiFileName"
    ),
    GET_SOBI_FRAGMENTS_BY_SOBI_FILE_AND_TYPE(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE sobi_file_name = :sobiFileName AND fragment_type = :fragmentType"
    ),
    GET_PENDING_SOBI_FRAGMENTS(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE pending_processing = true"
    ),
    GET_PENDING_SOBI_FRAGMENTS_BY_TYPE(
        GET_PENDING_SOBI_FRAGMENTS.sql + " AND fragment_type IN (:fragmentTypes)"
    ),
    UPDATE_SOBI_FRAGMENT(
        "UPDATE ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "SET sobi_file_name = :sobiFileName, published_date_time = :publishedDateTime, " +
        "    fragment_type = :fragmentType, text = :text, sequence_no = :sequenceNo, " +
        "    processed_count = :processedCount, processed_date_time = :processedDateTime, " +
        "    pending_processing = :pendingProcessing, manual_fix = :manualFix, manual_fix_notes = :manualFixNotes \n" +
        "WHERE fragment_id = :fragmentId"
    ),
    INSERT_SOBI_FRAGMENT(
        "INSERT INTO ${schema}." + SqlTable.SOBI_FRAGMENT +
        "(sobi_file_name, fragment_id, published_date_time, fragment_type, text, sequence_no, " +
        " processed_count, processed_date_time, pending_processing, manual_fix, manual_fix_notes)\n" +
        "VALUES (:sobiFileName, :fragmentId, :publishedDateTime, :fragmentType, :text, :sequenceNo," +
        "        :processedCount, :processedDateTime, :pendingProcessing, :manualFix, :manualFixNotes)"
    ),
    DELETE_SOBI_FRAGMENTS(
        "DELETE FROM ${schema}." + SqlTable.SOBI_FRAGMENT + " WHERE sobi_file_name = :sobiFileName"
    );

    private String sql;

    SqlSobiQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}