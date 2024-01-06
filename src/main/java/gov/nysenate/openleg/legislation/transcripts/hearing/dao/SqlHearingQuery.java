package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.HEARING;

public enum SqlHearingQuery implements BasicSqlQuery {
    RESET_ID (
        "ALTER SEQUENCE ${schema}.hearing_id_seq RESTART"
    ),
    SELECT_HEARING_IDS (
        "SELECT id FROM ${schema}." + HEARING
    ),
    SELECT_ID_BY_FILENAME(
            "SELECT id FROM ${schema}." + HEARING + "\n" +
            "WHERE filename = :filename"
    ),
    SELECT_FILENAME_BY_ID (
            "SELECT filename FROM ${schema}." + HEARING + "\n" +
            "WHERE id = :id"
    ),
    SELECT_HEARING_BY_ID (
        "SELECT * FROM ${schema}." + HEARING + "\n" +
        "WHERE id = :id"
    ),
    SELECT_HEARINGS (
            "SELECT * FROM ${schema}." + HEARING + "\n"
    ),
    SELECT_HEARINGS_BY_YEAR (
            "SELECT * FROM ${schema}." + HEARING + "\n" +
                    "WHERE EXTRACT(YEAR FROM date) = :year"
    ),
    UPDATE_HEARING (
        "UPDATE ${schema}." + HEARING + "\n" +
        "SET filename = :filename, title = :title, date = :date, address = :address, text = :text, " +
            "start_time = :startTime, end_time = :endTime, modified_date_time = :modifiedDateTime\n" +
        "WHERE filename LIKE :filenameRegex"
    ),
    INSERT_HEARING (
            "INSERT INTO ${schema}." + HEARING + "\n" +
                "(filename, date, title, address, text, start_time, end_time)\n" +
            "VALUES (:filename, :date, :title, :address, :text, :startTime, :endTime)"
    ),
    SELECT_HEARING_UPDATES (
        "SELECT id, filename, modified_date_time, COUNT(*) OVER() as total_updated " +
        "FROM ${schema}." + HEARING + "\n" +
        "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime");

    private final String sql;

    SqlHearingQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
