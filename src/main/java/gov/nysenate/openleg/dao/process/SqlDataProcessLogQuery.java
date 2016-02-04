package gov.nysenate.openleg.dao.process;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlDataProcessLogQuery implements BasicSqlQuery
{
    SELECT_DATA_PROCESS_RUN(
        "SELECT id, process_start_date_time, process_end_date_time, invoked_by, exceptions\n" +
        "FROM ${schema}." + SqlTable.DATA_PROCESS_RUN + "\n" +
        "WHERE id = :processId"
    ),

    SELECT_DATA_PROCESS_RUNS_DURING(
        "SELECT id, process_start_date_time, process_end_date_time, invoked_by, exceptions, " +
        "       COUNT(id) OVER () AS total_count\n" +
        "FROM ${schema}." + SqlTable.DATA_PROCESS_RUN + "\n" +
        "WHERE (process_start_date_time BETWEEN :startDateTime AND :endDateTime)"
    ),

    SELECT_DATA_PROCESS_RUNS_WITH_ACTIVITY(
        SELECT_DATA_PROCESS_RUNS_DURING.sql + "\n" +
        "AND (id IN (SELECT DISTINCT process_id FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + ")\n" +
        "     OR (exceptions IS NOT NULL AND exceptions != '')" +
        ")"
    ),

    INSERT_DATA_PROCESS_RUN(
        "INSERT INTO ${schema}." + SqlTable.DATA_PROCESS_RUN + "\n" +
        "(process_start_date_time, process_end_date_time, invoked_by, exceptions)\n" +
        "VALUES (:startDateTime, :endDateTime, :invokedBy, :exceptions)\n" +
        "RETURNING id"
    ),
    UPDATE_DATA_PROCESS_RUN(
        "UPDATE ${schema}." + SqlTable.DATA_PROCESS_RUN + "\n" +
        "SET process_start_date_time = :startDateTime, process_end_date_time = :endDateTime, \n" +
        "    invoked_by = :invokedBy, exceptions = :exceptions\n" +
        "WHERE id = :id"
    ),

    SELECT_DATA_PROCESS_UNITS(
        "SELECT process_id, source_type, source_id, action, start_date_time, end_date_time, errors, messages,\n" +
        "       COUNT(*) OVER () AS total_count\n" +
        "FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + "\n" +
        "WHERE process_id = :processId"
    ),

    SELECT_FIRST_AND_LAST_DATA_PROCESS_UNITS(
        "SELECT process_id, source_type, source_id, action, start_date_time, end_date_time, errors, messages\n" +
        "FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + "\n" +
        "WHERE process_id = :processId AND start_date_time = " +
            "(SELECT MIN(start_date_time) FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + " WHERE process_id = :processId)\n" +
        "OR start_date_time = " +
            "(SELECT MAX(start_date_time) FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + " WHERE process_id = :processId)\n" +
        "ORDER BY start_date_time ASC"
    ),

    DELETE_PROCESS_UNITS(
        "DELETE FROM ${schema}." + SqlTable.DATA_PROCESS_UNIT + "\n" +
        "WHERE process_id = :processId"
    ),
    INSERT_PROCESS_UNIT(
        "INSERT INTO ${schema}." + SqlTable.DATA_PROCESS_UNIT + "\n" +
        "(process_id, source_type, source_id, action, start_date_time, end_date_time, messages, errors)\n" +
        "VALUES (:processId, :sourceType, :sourceId, :action, :startDateTime, :endDateTime, :messages, :errors)"
    )
    ;
    private String sql;

    SqlDataProcessLogQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
