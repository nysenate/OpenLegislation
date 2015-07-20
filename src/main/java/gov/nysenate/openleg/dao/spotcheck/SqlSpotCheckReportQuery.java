package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.OpenMismatchQuery;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    /** --- Reports --- */

    SELECT_REPORT_SUMMARY_IDS(
        "SELECT *, (SELECT COUNT(*) FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " obs\n" +
        "       WHERE rep.id = obs.report_id) AS observation_count\n" +
        "FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " rep\n" +
        "WHERE report_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "   AND (:getAllRefTypes OR reference_type = :referenceType)"
    ),
    SELECT_REPORT_SUMMARIES(
        "SELECT r.*, tsc.* FROM (" + SELECT_REPORT_SUMMARY_IDS.sql + ") as r\n" +
        "LEFT JOIN (\n" +
        "   SELECT report_id, type, status, COUNT(m.id) AS mismatch_count\n" +
        "   FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m\n" +
        "       ON o.id = m.observation_id\n" +
        "   GROUP BY report_id, type, status\n" +
        ") AS tsc\n" +
        "   ON r.id = tsc.report_id"
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
        "SELECT m.id as mismatch_id, m.type, m.status, m.reference_data, m.observed_data, m.notes,\n" +
        "       o.report_id, o.reference_type, o.reference_active_date, o.key, o.observed_date_time,\n" +
        "       r.report_date_time, r.reference_type AS report_reference_type\n"
    ),
    OBS_MISMATCHES_FROM_CLAUSE(
        "FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " r\n" +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON o.report_id = r.id\n" +
        "LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id\n"
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
            "   AND m.observation_id = o.id" +
        ") q\n" +
        "ORDER BY current DESC"
    ),
    SELECT_LATEST_OBS_MISMATCHES(
        OBS_MISMATCHES_SELECT_CLAUSE.sql + ", COUNT(*) OVER () AS mismatch_count FROM (\n" +
        "       SELECT key, MAX(observed_date_time) AS latest_obs_date_time\n" +
        "       FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "       WHERE reference_type = :referenceType\n" +
        "       GROUP BY key) AS latest_obs\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o\n" +
        "       ON o.observed_date_time = latest_obs.latest_obs_date_time AND o.key = latest_obs.key\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " r on o.report_id = r.id\n" +
        "   LEFT JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id"
    ),
    SELECT_LATEST_OBS_MISMATCHES_PARTIAL(
        "SELECT latest_obs.*, true AS current, hstore_to_array(latest_obs.key) AS key_arr\n" +
        "FROM (\n" + SELECT_LATEST_OBS_MISMATCHES.sql + "\n) as latest_obs"
    ),
    SELECT_LATEST_OPEN_OBS_MISMATCHES(
        "SELECT q.*, hstore_to_array(q.key) AS key_arr FROM (\n" +
        "   WITH latest_obs AS (\n" +
        "       " + SELECT_LATEST_OBS_MISMATCHES.sql + "\n" +
        "       WHERE m.id IS NOT NULL ${typeFilter}${dateFilter}${resolvedFilter}${ignoredFilter}\n" +
        "       ${orderBy}\n" +
        "       ${limitOffset}\n" +
        "   )\n" +
        "   SELECT latest_obs.*, true AS current FROM latest_obs\n" +
        "   UNION\n" +
        "   " + OBS_MISMATCHES_SELECT_CLAUSE.sql + ", -1 AS mismatch_count, false AS current\n" +
        "   FROM latest_obs\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON latest_obs.key = o.key \n" +
        "       AND latest_obs.reference_type = o.reference_type\n" +
        "       AND latest_obs.report_id != o.report_id\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " r ON o.report_id = r.id\n" +
        "   JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id\n" +
        ") q\n" +
        "ORDER BY current DESC"
    ),
    SELECT_OBS_MISMATCHES_BY_TYPE(
        OBS_MISMATCHES_SELECT_CLAUSE.sql + OBS_MISMATCHES_FROM_CLAUSE.sql +
        "WHERE o.key = :key::hstore AND m.type = :type AND o.reference_type = :obsReferenceType"
    ),
    INSERT_MISMATCH(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_MISMATCH + "\n" +
        "(observation_id, type, status, reference_data, observed_data, notes)\n" +
        "VALUES (:observationId, :type, :status, :referenceData, :observedData, :notes)"
    );

    private String sql;

    SqlSpotCheckReportQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    public static String getLatestOpenObsMismatchesQuery(String schema, OpenMismatchQuery query) {
        Map<String, String> subMap = ImmutableMap.<String, String>builder()
                .put("typeFilter", query.getMismatchTypes() != null && !query.getMismatchTypes().isEmpty()
                        ? " AND m.type IN (:mismatchTypes)" : "")
                .put("dateFilter", query.getObservedAfter() != null ? " AND o.observed_date_time >= :earliest" : "")
                .put("resolvedFilter", query.isResolvedShown() ? "" : " AND m.status != 'RESOLVED'")
                .put("ignoredFilter", query.isIgnoredShown() ? "" : " AND m.status != 'IGNORED'")
                .put("orderBy", SqlQueryUtils.getOrderByClause(query.getFullOrderBy()))
                .put("limitOffset", SqlQueryUtils.getLimitOffsetClause(query.getLimitOffset()))
                .build();
        return StrSubstitutor.replace(SELECT_LATEST_OPEN_OBS_MISMATCHES.getSql(schema), subMap);
    }
}
