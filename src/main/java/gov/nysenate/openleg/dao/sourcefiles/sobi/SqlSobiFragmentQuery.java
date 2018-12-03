package gov.nysenate.openleg.dao.sourcefiles.sobi;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlSobiFragmentQuery implements BasicSqlQuery
{

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
        "    pending_processing = :pendingProcessing, manual_fix = :manualFix, manual_fix_notes = :manualFixNotes,\n" +
        "    process_start_date_time = :processStartDateTime\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    INSERT_SOBI_FRAGMENT(
        "INSERT INTO ${schema}." + SqlTable.SOBI_FRAGMENT +
        "(sobi_file_name, fragment_id, published_date_time, fragment_type, text, sequence_no, " +
        " processed_count, processed_date_time, pending_processing, manual_fix, manual_fix_notes,\n" +
        " process_start_date_time)\n" +
        "VALUES (:sobiFileName, :fragmentId, :publishedDateTime, :fragmentType, :text, :sequenceNo,\n" +
        "        :processedCount, :processedDateTime, :pendingProcessing, :manualFix, :manualFixNotes,\n" +
        "        :processStartDateTime)"
    ),
    DELETE_SOBI_FRAGMENTS(
        "DELETE FROM ${schema}." + SqlTable.SOBI_FRAGMENT + " WHERE sobi_file_name = :sobiFileName"
    );

    private String sql;

    SqlSobiFragmentQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}