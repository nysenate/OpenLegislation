package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.MismatchQuery;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    INSERT_REPORT(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_REPORT + " (report_date_time, reference_date_time, reference_type, notes)\n" +
        "VALUES (:reportDateTime, :referenceDateTime, :referenceType, :notes)"
    ),

    GET_MISMATCH(
        "SELECT m.mismatch_id, m.report_id, hstore_to_array(m.key) as key, m.type, m.status, \n" +
        "m.datasource, m.content_type, m.reference_type, m.reference_active_date_time, m.reference_data, m.observed_data, m.notes, \n" +
        "m.observed_date_time, m.report_date_time, m.ignore_status, m.issue_ids \n" +
        "  FROM ${schema}.spotcheck_mismatch m \n" +
        "  WHERE m.mismatch_id = :mismatchId \n"
    ),

    GET_MISMATCHES(
        "SELECT *, count(*) OVER() as total_rows FROM \n" +
        "  (SELECT DISTINCT ON (m.key, m.type) m.mismatch_id, m.report_id, hstore_to_array(m.key) as key, m.type, m.status, \n" +
        "  m.datasource, m.content_type, m.reference_type, m.reference_active_date_time, m.reference_data, m.observed_data, m.notes, \n" +
        "  m.observed_date_time, m.report_date_time, m.ignore_status, m.issue_ids \n" +
        "    FROM ${schema}.spotcheck_mismatch m \n" +
        "    WHERE m.reference_active_date_time BETWEEN :fromDate AND :toDate \n" +
        "      AND m.datasource = :datasource \n" +
        "      AND m.content_type IN (:contentTypes) \n" +
        "      AND m.ignore_status IN (:ignoreStatuses) \n" +
        "    ORDER BY m.key, m.type, m.reference_active_date_time desc \n" +
        "  ) open_mismatches \n" +
        "WHERE status IN (:statuses)"
    ),

    INSERT_MISMATCH(
        "INSERT INTO ${schema}.spotcheck_mismatch\n" +
        "(key, type, report_id, datasource, content_type, reference_type,\n" +
        "status, reference_data, observed_data, notes, issue_ids, ignore_status,\n" +
        "report_date_time, observed_date_time, reference_active_date_time)\n" +
        "VALUES\n" +
        "(:key::hstore, :mismatchType, :reportId, :datasource, :contentType, :referenceType, \n" +
        ":mismatchStatus, :referenceData, :observedData, :notes, :issueIds::text[], :ignoreLevel, \n" +
        ":reportDateTime, :observedDateTime, :referenceActiveDateTime)\n"
    ),

    MISMATCH_SUMMARY(
        "SELECT *, count(*) FROM\n"+
        "  (SELECT content_type, status FROM\n"+
        "    (SELECT DISTINCT ON (m.key, m.type, m.datasource) m.content_type, m.status, m.reference_active_date_time\n"+
        "     FROM ${schema}.spotcheck_mismatch m\n"+
        "     WHERE m.reference_active_date_time BETWEEN :fromDate AND :toDate\n"+
        "       AND m.datasource = :datasource\n"+
        "       AND m.ignore_status = 'NOT_IGNORED'\n"+
        "     ORDER BY m.key, m.type, m.datasource, m.reference_active_date_time desc\n"+
        "    ) most_recent_mismatches\n"+
        "  WHERE status != 'RESOLVED'\n"+
        "    OR reference_active_date_time > :startOfToDate\n"+
        "  ) summary\n"+
        "GROUP BY content_type, status\n"
    ),

    UPDATE_MISMATCH_IGNORE(
        "UPDATE ${schema}.spotcheck_mismatch\n" +
        "SET ignore_status = :ignoreStatus\n" +
        "WHERE mismatch_id = :mismatchId\n"
    ),

    UPDATE_ISSUE_IDS(
        "UPDATE ${schema}.spotcheck_mismatch\n" +
        "SET issue_ids = :issueIds::text[]\n" +
        "WHERE mismatch_id = :mismatchId\n"
    )
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
