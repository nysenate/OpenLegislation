package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.*;

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
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_REPORT + " (report_date_time, reference_type)\n" +
        "VALUES (:reportDateTime, :referenceType)"
    ),
    DELETE_REPORT(
        "DELETE ${schema}." + SqlTable.SPOTCHECK_REPORT + "\n" + WHERE_REPORT_CLAUSE.sql
    ),

    /** --- Observations --- */

    SELECT_OBSERVATIONS(
        "SELECT * FROM ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "WHERE report_id IN (" + SELECT_REPORT_ID.sql + ")"
    ),
    INSERT_OBSERVATION_AND_RETURN_ID(
        "INSERT INTO ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + "\n" +
        "(report_id, reference_type, reference_active_date, key, observed_date_time)\n" +
        "SELECT r.id, :referenceType, :referenceActiveDate, :key, :observedDateTime\n" +
        "FROM (" + SELECT_REPORT_ID.sql + ") r" +
        "RETURNING id"
    ),

    /** --- Mismatches --- */

    SELECT_OBSERVATIONS_MISMATCHES(
        "SELECT m.type, m.status, m.reference_data, m.observed_data, m.notes, \n" +
        "       o.report_id, o.reference_type, o.reference_active_date, o.key, o.observed_date_time\n" +
        "FROM ${schema}." + SqlTable.SPOTCHECK_MISMATCH + "m " +
        "JOIN ${schema}." + SqlTable.SPOTCHECK_OBSERVATION + " o ON m.observation_id = o.id\n" +
        "WHERE o.report_id IN (" + SELECT_REPORT_ID.sql + ")"
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
