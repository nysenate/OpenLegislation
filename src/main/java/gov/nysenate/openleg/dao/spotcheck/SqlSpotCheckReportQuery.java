package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlSpotCheckReportQuery implements BasicSqlQuery
{
    /** --- Reports --- */

    SELECT_REPORTS(
        "SELECT * FROM ${schema}." + SqlTable.SPOTCHECK_REPORT
    ),
    SELECT_REPORTS_BY_DATE(
        SELECT_REPORTS.sql + " WHERE report_date_time BETWEEN :startDateTime AND :endDateTime"
    ),
    SELECT_REPORTS_BY_DATE_AND_TYPE(
        SELECT_REPORTS_BY_DATE.sql + " AND reference_type = :referenceType"
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
        "SELECT m.type, m.status, m.reference_data, m.observed_data, m.notes,\n" +
        "       o.report_id, o.reference_type, o.reference_active_date, o.key, o.observed_date_time, " +
        "       r.report_date_time, r.reference_type AS report_reference_type\n"
    ),
    OBS_MISMATCHES_FROM_CLAUSE(
        "FROM ${schema}." + SqlTable.SPOTCHECK_REPORT + " r\n" +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON o.report_id = r.id\n" +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON m.observation_id = o.id\n"
    ),
    SELECT_OBS_MISMATCHES_BY_REPORT(
        // Have to use 'hstore_to_array' to parse the hstore key
        "SELECT q.*, hstore_to_array(q.key) AS key_arr FROM (\n" +
            // Select a specific report
            "WITH report_obs AS (\n" +
                OBS_MISMATCHES_SELECT_CLAUSE.sql + OBS_MISMATCHES_FROM_CLAUSE.sql +
                "WHERE r.report_date_time = :reportDateTime AND r.reference_type = :referenceType\n" +
            ")\n" +
            "SELECT report_obs.*, true AS current FROM report_obs\n" +
            // ..and also fetch mismatch records that have the same type but occurred on an earlier report
            "UNION\n" +
            OBS_MISMATCHES_SELECT_CLAUSE.sql + ", false AS current\n" +
            "FROM report_obs\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON report_obs.key = o.key " +
            "   AND report_obs.reference_type = o.reference_type\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_REPORT + " r ON o.report_id = r.id AND report_obs.report_id != r.id\n" +
            "   AND r.report_date_time < report_obs.report_date_time\n" +
            "JOIN ${schema}." + SqlTable.SPOTCHECK_MISMATCH + " m ON report_obs.type = m.type\n" +
            "   AND m.observation_id = o.id" +
        ") q"
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
}
