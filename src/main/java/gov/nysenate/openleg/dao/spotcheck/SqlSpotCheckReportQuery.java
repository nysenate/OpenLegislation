package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.MismatchQuery;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    /** Partial query used to get all the most recent mismatches as of :toDateTime. */
    ACTIVE_MISMATCHS(
            "SELECT DISTINCT ON (key, type) * \n" +
            "FROM ${schema}.spotcheck_mismatch \n" +
            "WHERE reference_active_date_time BETWEEN :sessionStartDateTime AND :reportEndDateTime \n" +
            "  AND datasource = :datasource \n" +
            "ORDER BY key, type, reference_active_date_time desc"
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
        "SELECT *, count(*) OVER() as total_rows FROM \n" +
        "  (SELECT DISTINCT ON (m.key, m.type) m.mismatch_id, m.report_id, m.key as key, m.type, m.state, \n" +
        "  m.datasource, m.content_type, m.reference_type, m.reference_active_date_time, m.reference_data, m.observed_data, m.notes, \n" +
        "  m.observed_date_time, m.report_date_time, m.ignore_status, m.issue_ids \n" +
        "    FROM ${schema}.spotcheck_mismatch m \n" +
        "    WHERE m.reference_active_date_time BETWEEN :fromDate AND :toDate \n" +
        "      AND m.datasource = :datasource \n" +
        "      AND m.content_type IN (:contentTypes) \n" +
        "    ORDER BY m.key, m.type, m.reference_active_date_time desc \n" +
        "  ) open_mismatches \n" +
        "WHERE state IN (:states)\n" +
        "AND ignore_status IN (:ignoreStatuses) AND type like :mismatchtype"
    ),

    INSERT_MISMATCH(
        "INSERT INTO ${schema}.spotcheck_mismatch\n" +
        "(key, type, report_id, datasource, content_type, reference_type,\n" +
        "state, reference_data, observed_data, notes, issue_ids, ignore_status,\n" +
        "report_date_time, observed_date_time, reference_active_date_time)\n" +
        "VALUES\n" +
        "(:key::hstore, :mismatchType, :reportId, :datasource, :contentType, :referenceType, \n" +
        ":mismatchStatus, :referenceData, :observedData, :notes, :issueIds::text[], :ignoreLevel, \n" +
        ":reportDateTime, :observedDateTime, :referenceActiveDateTime)\n"
    ),

    MISMATCH_STATUS_SUMMARY(
            "SELECT 'NEW' as status, count(*) as count \n" +
                    "FROM (" + ACTIVE_MISMATCHS.getSql() + ") active_mismatches \n" +
                    "WHERE observed_date_time BETWEEN :reportStartDateTime AND :reportEndDateTime \n" +
                    "AND state = 'OPEN'\n" +
                    "UNION ALL \n" +
                    "SELECT 'RESOLVED', count(*) \n" +
                    "FROM (" + ACTIVE_MISMATCHS.getSql() + ") active_mismatches \n" +
                    "WHERE observed_date_time BETWEEN :reportStartDateTime AND :reportEndDateTime \n" +
                    "AND state = 'CLOSED'\n" +
                    "UNION ALL \n" +
                    "SELECT 'EXISTING', count(*) \n" +
                    "FROM (" + ACTIVE_MISMATCHS.getSql() + ") active_mismatches \n" +
                    "WHERE observed_date_time BETWEEN :sessionStartDateTime AND :reportStartDateTime \n" +
                    "AND state = 'OPEN'\n"
    ),

    MISMATCH_TYPE_SUMMARY(
            "SELECT type, count(type) FROM\n" +
            "    (SELECT DISTINCT ON (m.key, m.type, m.datasource) m.type, m.state, m.reference_active_date_time, m.ignore_status\n" +
            "     FROM  ${schema}.spotcheck_mismatch m\n" +
            "     WHERE m.reference_active_date_time BETWEEN :fromDate AND :toDate\n" +
            "       AND m.datasource =  :datasource\n" +
            "     ORDER BY m.key, m.type, m.datasource, m.reference_active_date_time desc\n" +
            "    ) most_recent_mismatches\n" +
            "  WHERE (state = :states\n" +
                    "    AND reference_active_date_time > :startOfToDate)\n"+
                    "    AND ignore_status = 'NOT_IGNORED'\n"+
            "    GROUP BY most_recent_mismatches.type\n"
    ),

    MISMATCH_CONTENTTYPE_SUMMARY(
            "SELECT type, count(type) FROM\n" +
                    "    (SELECT DISTINCT ON (m.key, m.type, m.datasource) m.type, m.state, m.reference_active_date_time, m.ignore_status\n" +
                    "     FROM  ${schema}.spotcheck_mismatch m\n" +
                    "     WHERE m.reference_active_date_time BETWEEN :fromDate AND :toDate\n" +
                    "       AND m.datasource =  :datasource\n" +
                    "       AND m.type like :mismatchtype\n" +
                    "     ORDER BY m.key, m.type, m.datasource, m.reference_active_date_time desc\n" +
                    "    ) most_recent_mismatches\n" +
                    "  WHERE (state = :states\n" +
                    "    AND reference_active_date_time > :startOfToDate)\n"+
                    "    AND ignore_status = 'NOT_IGNORED'\n"+
                    "    GROUP BY most_recent_mismatches.type\n"
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
