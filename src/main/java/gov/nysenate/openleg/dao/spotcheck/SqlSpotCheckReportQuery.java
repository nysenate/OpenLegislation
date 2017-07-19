package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.*;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    /** Partial query used to get all the most recent mismatches as of :reportEndDateTime. */
    ACTIVE_MISMATCHES(
            "SELECT DISTINCT ON (key, type) * \n" +
            "FROM ${schema}.spotcheck_mismatch \n" +
            "WHERE observed_date_time BETWEEN :sessionStartDateTime AND :reportEndDateTime \n" +
            "  AND datasource = :datasource \n" +
            "ORDER BY key, type, observed_date_time desc "
    ),

    INSERT_REPORT(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_REPORT + " (report_date_time, reference_date_time, reference_type, notes)\n" +
        "VALUES (:reportDateTime, :referenceDateTime, :referenceType, :notes)"
    ),

    GET_MISMATCH(
        "SELECT m.mismatch_id, m.report_id, m.key as key, m.type, m.state, \n" +
        "m.datasource, m.content_type, m.reference_type, m.reference_active_date_time, m.reference_data, m.observed_data, m.notes, \n" +
        "m.observed_date_time, m.report_date_time, m.ignore_status, m.issue_ids \n" +
        "  FROM ${schema}.spotcheck_mismatch m \n" +
        "  WHERE m.mismatch_id = :mismatchId \n"
    ),

    GET_MISMATCHES(
        "SELECT mismatch_id, report_id, key, type, state, datasource, content_type, \n" +
        "  reference_type, reference_active_date_time, reference_data, observed_data, notes, \n" +
        "  observed_date_time, first_seen_date_time, report_date_time, ignore_status, issue_ids, \n" +
        "  count(*) OVER() as total_rows \n" +
        "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
        "WHERE first_seen_date_time BETWEEN :firstSeenStartDateTime AND :endDateTime \n" +
        "  AND observed_date_time BETWEEN :observedStartDateTime AND :endDateTime \n" +
        "  AND state = :state \n" +
        "  AND content_type IN (:contentTypes) \n" +
        "  AND type IN (:mismatchTypes) \n" +
        "  AND ignore_status IN (:ignoreStatuses)\n"
    ),

    INSERT_MISMATCH(
        "INSERT INTO ${schema}.spotcheck_mismatch\n" +
        "(key, type, report_id, datasource, content_type, reference_type,\n" +
        "state, reference_data, observed_data, notes, issue_ids, ignore_status,\n" +
        "report_date_time, observed_date_time, reference_active_date_time, first_seen_date_time)\n" +
        "VALUES\n" +
        "(:key::hstore, :mismatchType, :reportId, :datasource, :contentType, :referenceType, \n" +
        ":mismatchStatus, :referenceData, :observedData, :notes, :issueIds::text[], :ignoreLevel, \n" +
        ":reportDateTime, :observedDateTime, :referenceActiveDateTime, :firstSeenDateTime)\n"
    ),

    MISMATCH_STATUS_SUMMARY(
            "SELECT 'NEW' as status, count(*) as count \n" +
            "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
            "WHERE first_seen_date_time BETWEEN :reportStartDateTime AND :reportEndDateTime \n" +
            "  AND ignore_status IN (:ignoreStatuses)\n" +
            "  AND state = 'OPEN'\n" +
            "UNION ALL \n" +
            "SELECT 'RESOLVED', count(*) \n" +
            "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
            "WHERE observed_date_time BETWEEN :reportStartDateTime AND :reportEndDateTime \n" +
            "  AND state = 'CLOSED'\n" +
            "  AND ignore_status IN (:ignoreStatuses)\n" +
            "UNION ALL \n" +
            "SELECT 'EXISTING', count(*) \n" +
            "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
            "WHERE first_seen_date_time BETWEEN :sessionStartDateTime AND :reportStartDateTime \n" +
            "  AND state = 'OPEN' \n" +
            "  AND ignore_status IN (:ignoreStatuses) \n"
    ),

    MISMATCH_TYPE_SUMMARY(
            "SELECT type, count(*) as count \n" +
            "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
            "WHERE first_seen_date_time BETWEEN :firstSeenStartDateTime AND :endDateTime \n" +
            "  AND observed_date_time BETWEEN :observedStartDateTime AND :endDateTime \n" +
            "  AND state = :state \n" +
            "  AND ignore_status IN (:ignoreStatuses) \n" +
            "GROUP BY type"
    ),

    MISMATCH_CONTENT_TYPE_SUMMARY(
            "SELECT content_type, count(*) as count \n" +
            "FROM (" + ACTIVE_MISMATCHES.getSql() + ") active_mismatches \n" +
            "WHERE first_seen_date_time BETWEEN :firstSeenStartDateTime AND :endDateTime \n" +
            "  AND observed_date_time BETWEEN :observedStartDateTime AND :endDateTime \n" +
            "  AND state = :state \n" +
            "  AND type IN (:mismatchTypes) \n" +
            "  AND ignore_status IN (:ignoreStatuses) \n" +
            "GROUP BY content_type"
    ),

    UPDATE_MISMATCH_IGNORE(
        "UPDATE ${schema}.spotcheck_mismatch\n" +
        "SET ignore_status = :ignoreStatus\n" +
        "WHERE mismatch_id = :mismatchId\n"
    ),

    UPDATE_ISSUE_ID(
        "UPDATE ${schema}.spotcheck_mismatch\n" +
        "SET issue_ids = string_to_array(:issueId::text, ',')\n" +
        "WHERE mismatch_id = :mismatchId\n"
    ),

    ADD_ISSUE_ID(
            "UPDATE ${schema}.spotcheck_mismatch\n" +
                    "SET issue_ids = array_append(issue_ids, :issueId::text)\n" +
                    "WHERE mismatch_id = :mismatchId\n"
    ),

    DELETE_ISSUE_ID(
        "UPDATE ${schema}.spotcheck_mismatch\n" +
        "SET issue_ids = array_remove(issue_ids, :issueId::text)\n" +
        "WHERE mismatch_id = :mismatchId\n"
    ),

    DELETE_ALL_ISSUE_ID(
            "UPDATE ${schema}.spotcheck_mismatch\n" +
                    "SET issue_ids =  ARRAY[]::text[]\n" +
                    "WHERE mismatch_id = :mismatchId\n"
    )
    ;
    ;

    private String sql;

    SqlSpotCheckReportQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

}
