package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    /** --- Reports --- */

    SELECT_REPORT_SUMMARIES(
        "SELECT * FROM (\n" +
        "   SELECT r.id as report_id, type, status, ignore_level, issue_id IS NOT NULL AS tracked,\n" +
        "       COUNT(m.id) AS mismatch_count\n" +
        "   FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " r\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o\n" +
        "       ON r.id = o.report_id" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m\n" +
        "       ON o.id = m.observation_id\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " i\n" +
        "       ON o.key = i.key AND m.type = i.mismatch_type AND o.reference_type = i.reference_type\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + " issue\n" +
        "       ON issue.mismatch_id = m.id\n" +
        "   WHERE r.report_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "       AND (:getAllRefTypes OR r.reference_type = :referenceType)\n" +
        "   GROUP BY r.id, type, status, ignore_level, tracked\n" +
        ") AS summary_counts\n" +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " rep\n" +
        "   ON summary_counts.report_id = rep.id"
    ),
    WHERE_REPORT_CLAUSE(
        "WHERE report_date_time = :reportDateTime AND reference_type = :referenceType"
    ),
    SELECT_REPORT(
        "SELECT * FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + "\n" + WHERE_REPORT_CLAUSE.sql
    ),
    SELECT_REPORT_ID(
        "SELECT id FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + "\n" + WHERE_REPORT_CLAUSE.sql
    ),
    INSERT_REPORT(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_REPORT + " (report_date_time, reference_date_time, reference_type, notes)\n" +
        "VALUES (:reportDateTime, :referenceDateTime, :referenceType, :notes)"
    ),
    DELETE_REPORT(
        "DELETE FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + "\n" + WHERE_REPORT_CLAUSE.sql
    ),

    /** --- Observations / Mismatches --- */

    SELECT_OBSERVATIONS(
        "SELECT * FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "WHERE report_id IN (" + SELECT_REPORT_ID.sql + ")"
    ),
    INSERT_OBSERVATION_AND_RETURN_ID(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "(report_id, reference_type, reference_active_date, key, observed_date_time)\n" +
        "SELECT r.id, :obsReferenceType, :referenceActiveDate, :key::hstore, :observedDateTime\n" +
        "FROM (" + SELECT_REPORT_ID.sql + ") r\n" +
        "RETURNING id"
    ),
    OBS_MISMATCHES_SELECT_CLAUSE(
        "SELECT m.id as mismatch_id, m.type, m.status, m.reference_data, m.observed_data, m.notes, issue.issue_id,\n" +
        "       o.report_id, o.reference_type, o.reference_active_date, o.key, o.observed_date_time,\n" +
        "       r.report_date_time, r.reference_type AS report_reference_type, i.ignore_level\n"
    ),
    OBS_MISMATCHES_FROM_CLAUSE(
        "FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " r\n" +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON o.report_id = r.id\n" +
        "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id\n" +
        "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " i\n" +
        "   ON o.key = i.key AND m.type = i.mismatch_type AND o.reference_type = i.reference_type\n" +
        "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + " issue\n" +
        "   ON issue.mismatch_id = m.id\n"
    ),
    SELECT_OBS_MISMATCHES_BY_REPORT(
        // Have to use 'hstore_to_array' to parse the hstore key
        "SELECT q.*, hstore_to_array(q.key) AS key_arr FROM (\n" +
            // Select a specific report
            "WITH report_obs AS (\n" +
                OBS_MISMATCHES_SELECT_CLAUSE.sql + ", COUNT(*) OVER () AS mismatch_count " + OBS_MISMATCHES_FROM_CLAUSE.sql +
                "WHERE r.report_date_time = :reportDateTime AND r.reference_type = :referenceType\n" +
            ")\n" +
            "SELECT report_obs.*, true AS current FROM report_obs\n" +
            // ..and also fetch mismatch records that have the same type but occurred on an earlier report
            "UNION\n" +
            OBS_MISMATCHES_SELECT_CLAUSE.sql + ", -1 AS mismatch_count, false AS current\n" +
            "FROM report_obs\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON report_obs.key = o.key " +
            "   AND report_obs.reference_type = o.reference_type\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " r ON o.report_id = r.id AND report_obs.report_id != r.id\n" +
            "   AND r.report_date_time < report_obs.report_date_time\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON report_obs.type = m.type\n" +
            "   AND m.observation_id = o.id\n" +
            "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " i\n" +
            "   ON o.key = i.key AND m.type = i.mismatch_type AND o.reference_type = i.reference_type\n" +
            "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + " issue\n" +
            "   ON issue.mismatch_id = m.id\n" +
        ") q\n" +
        "ORDER BY current DESC"
    ),
    SELECT_LATEST_OBS_MISMATCHES(
        OBS_MISMATCHES_SELECT_CLAUSE.sql + ", COUNT(*) OVER () AS mismatch_count\n" +
        "   FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " r\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o\n" +
        "       ON r.id = o.report_id\n" +
        "   JOIN (\n" +
        "       SELECT reference_type, key, MAX(observed_date_time) AS latest_obs_date_time\n" +
        "       FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "       GROUP BY reference_type, key\n" +
        "   ) AS latest_obs\n" +
        "       ON latest_obs.reference_type = o.reference_type AND \n" +
        "            o.key = latest_obs.key AND o.observed_date_time = latest_obs.latest_obs_date_time\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " i\n" +
        "       ON o.key = i.key AND m.type = i.mismatch_type AND o.reference_type = i.reference_type" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + " issue\n" +
        "       ON issue.mismatch_id = m.id\n" +
        "   WHERE r.reference_type IN (:referenceTypes)\n"
    ),
    SELECT_LATEST_OBS_MISMATCHES_PARTIAL(
        "SELECT latest_obs.*, true AS current, hstore_to_array(latest_obs.key) AS key_arr\n" +
        "FROM (\n" + SELECT_LATEST_OBS_MISMATCHES.sql + "\n) as latest_obs"
    ),
    SELECT_LATEST_OPEN_OBS_MISMATCHES_SUB_QUERY(
        "   WITH latest_obs AS (\n" +
        "       " + SELECT_LATEST_OBS_MISMATCHES.sql + "\n" +
        "           AND m.id IS NOT NULL ${typeFilter}${dateFilter}${resolvedFilter}${ignoredFilter}${trackedFilter}${untrackedFilter}\n" +
        "       ${orderBy}\n" +
        "       ${limitOffset}\n" +
        "   )\n"
    ),
    SELECT_LATEST_OPEN_OBS_MISMATCHES(
        "SELECT q.*, hstore_to_array(q.key) AS key_arr FROM (\n" +
            SELECT_LATEST_OPEN_OBS_MISMATCHES_SUB_QUERY.sql +
        "   SELECT latest_obs.*, true AS current FROM latest_obs\n" +
        "   UNION\n" +
        "   " + OBS_MISMATCHES_SELECT_CLAUSE.sql + ", -1 AS mismatch_count, false AS current\n" +
        "   FROM latest_obs\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON latest_obs.key = o.key \n" +
        "       AND latest_obs.reference_type = o.reference_type\n" +
        "       AND latest_obs.report_id != o.report_id\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " r ON o.report_id = r.id\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " i\n" +
        "       ON o.key = i.key AND m.type = i.mismatch_type AND o.reference_type = i.reference_type\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + " issue\n" +
        "       ON issue.mismatch_id = m.id\n" +
        ") q\n" +
        "ORDER BY current DESC"
    ),
    SELECT_LATEST_OPEN_OBS_MISMATCHES_SUMMARY(
        SELECT_LATEST_OPEN_OBS_MISMATCHES_SUB_QUERY.sql +
        "SELECT reference_type, status, type, ignore_level, issue_id IS NOT NULL AS tracked,\n" +
        "   count(*) AS mismatch_count\n" +
        "FROM latest_obs\n" +
        "GROUP BY reference_type, status, type, ignore_level, tracked"
    ),
    SELECT_OBS_MISMATCHES_BY_TYPE(
        OBS_MISMATCHES_SELECT_CLAUSE.sql + OBS_MISMATCHES_FROM_CLAUSE.sql +
        "WHERE o.key = :key::hstore AND m.type = :type AND o.reference_type = :obsReferenceType"
    ),
    INSERT_MISMATCH(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_MISMATCH + "\n" +
        "(observation_id, type, status, reference_data, observed_data, notes)\n" +
        "VALUES (:observationId, :type, :status, :referenceData, :observedData, :notes)"
    ),

    /** --- Mismatch Ignore queries --- */

    MISMATCH_ID_SUBQUERY(
        "WITH mm_id_fields AS (\n" +
        "   SELECT key, type, reference_type\n" +
        "   FROM ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " mm\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " obs\n" +
        "       ON mm.observation_id = obs.id\n" +
        "   WHERE mm.id = :mismatchId\n" +
        ")\n"
    ),
    INSERT_MISMATCH_IGNORE(
        MISMATCH_ID_SUBQUERY.getSql() +
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + "\n" +
        "       (key, mismatch_type, reference_type, ignore_level)\n" +
        "SELECT  key, type, reference_type, :ignoreLevel\n" +
        "FROM mm_id_fields"
    ),
    UPDATE_MISMATCH_IGNORE(
        MISMATCH_ID_SUBQUERY.getSql() +
        "UPDATE ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " ig\n" +
        "SET ignore_level = :ignoreLevel\n" +
        "FROM mm_id_fields\n" +
        "WHERE ig.reference_type = mm_id_fields.reference_type\n" +
        "   AND ig.key = mm_id_fields.key\n" +
        "   AND ig.mismatch_type = mm_id_fields.type"
    ),
    DELETE_MISMATCH_IGNORE(
        MISMATCH_ID_SUBQUERY.getSql() +
        "DELETE FROM ${schema}." + SqlTable.SPOTCHECK_MISMATCH_IGNORE + " ig\n" +
        "USING mm_id_fields\n" +
        "WHERE ig.reference_type = mm_id_fields.reference_type\n" +
        "   AND ig.key = mm_id_fields.key\n" +
        "   AND ig.mismatch_type = mm_id_fields.type"
    ),

    /** --- Issue Id Queries --- */

    ADD_ISSUE_ID(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + "(mismatch_id, issue_id)\n" +
        "VALUES (:mismatchId, :issueId)\n" +
        "ON CONFLICT DO NOTHING"
    ),

    DELETE_ISSUE_ID(
        "DELETE FROM ${schema}." + SqlTable.SPOTCHECK_MISMATCH_ISSUE_ID + "\n" +
        "WHERE mismatch_id = :mismatchId AND issue_id = :issueId"
    ),

    ;

    private String sql;

    SqlSpotCheckReportQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    public static String getLatestOpenObsMismatchesQuery(String schema, OpenMismatchQuery query) {
        Map<String, String> subMap = getOpenMismatchQuerySubMap(query);
        return StrSubstitutor.replace(SELECT_LATEST_OPEN_OBS_MISMATCHES.getSql(schema), subMap);
    }

    public static String getOpenObsMismatchesSummaryQuery(String schema, OpenMismatchQuery query) {
        Map<String, String> subMap = getOpenMismatchQuerySubMap(query);
        return StrSubstitutor.replace(SELECT_LATEST_OPEN_OBS_MISMATCHES_SUMMARY.getSql(schema), subMap);
    }

    private static Map<String, String> getOpenMismatchQuerySubMap(OpenMismatchQuery query) {
        return ImmutableMap.<String, String>builder()
                .put("typeFilter", query.getMismatchTypes() != null && !query.getMismatchTypes().isEmpty()
                        ? " AND m.type IN (:mismatchTypes)" : "")
                .put("dateFilter", query.getObservedAfter() != null ? " AND o.observed_date_time >= :earliest" : "")
                .put("resolvedFilter", query.isResolvedShown() ? "" : " AND m.status != 'RESOLVED'")
                .put("ignoredFilter", query.isIgnoredOnly()
                        ? " AND i.ignore_level IS NOT NULL"
                        : query.isIgnoredShown() ? "" : " AND i.ignore_level IS NULL")
                .put("trackedFilter", query.isTrackedShown() ? "" : " AND issue_id IS NULL")
                .put("untrackedFilter", query.isUntrackedShown() ? "" : " AND issue_id IS NOT NULL")
                .put("orderBy", SqlQueryUtils.getOrderByClause(query.getFullOrderBy()))
                .put("limitOffset", SqlQueryUtils.getLimitOffsetClause(query.getLimitOffset()))
                .build();
    }
}
