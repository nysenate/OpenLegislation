package gov.nysenate.openleg.dao.sourcefiles.sobi;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLegDataFragmentQuery implements BasicSqlQuery
{

    /** --- Leg Data Fragments --- */

    CHECK_LEG_DATA_FRAGMENT_EXISTS(
        "SELECT 1 FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    GET_LEG_DATA_FRAGMENT_BY_FILE_NAME(
        "SELECT * FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    GET_LEG_DATA_FRAGMENTS_BY_LEG_DATA_FILE(
        "SELECT * FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "WHERE leg_data_file_name = :legDataFileName"
    ),
    GET_LEG_DATA_FRAGMENTS_BY_LEG_DATA_FILE_AND_TYPE(
        "SELECT * FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "WHERE leg_data_file_name = :legDataFileName AND fragment_type = :fragmentType"
    ),
    GET_PENDING_LEG_DATA_FRAGMENTS(
        "SELECT * FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "WHERE pending_processing = true"
    ),
    GET_PENDING_LEG_DATA_FRAGMENTS_BY_TYPE(
        GET_PENDING_LEG_DATA_FRAGMENTS.sql + " AND fragment_type IN (:fragmentTypes)"
    ),
    UPDATE_LEG_DATA_FRAGMENT(
        "UPDATE ${schema}." + SqlTable.LEG_DATA_FRAGMENT + "\n" +
        "SET leg_data_file_name = :legDataFileName, published_date_time = :publishedDateTime, " +
        "    fragment_type = :fragmentType, text = :text, sequence_no = :sequenceNo, " +
        "    processed_count = :processedCount, processed_date_time = :processedDateTime, " +
        "    pending_processing = :pendingProcessing, manual_fix = :manualFix, manual_fix_notes = :manualFixNotes,\n" +
        "    process_start_date_time = :processStartDateTime\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    INSERT_LEG_DATA_FRAGMENT(
        "INSERT INTO ${schema}." + SqlTable.LEG_DATA_FRAGMENT +
        "(leg_data_file_name, fragment_id, published_date_time, fragment_type, text, sequence_no, " +
        " processed_count, processed_date_time, pending_processing, manual_fix, manual_fix_notes,\n" +
        " process_start_date_time)\n" +
        "VALUES (:legDataFileName, :fragmentId, :publishedDateTime, :fragmentType, :text, :sequenceNo,\n" +
        "        :processedCount, :processedDateTime, :pendingProcessing, :manualFix, :manualFixNotes,\n" +
        "        :processStartDateTime)"
    ),
    DELETE_LEG_DATA_FRAGMENTS(
        "DELETE FROM ${schema}." + SqlTable.LEG_DATA_FRAGMENT + " WHERE leg_data_file_name = :legDataFileName"
    );

    private String sql;

    SqlLegDataFragmentQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}