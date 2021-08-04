package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlPublicHearingQuery implements BasicSqlQuery
{
    SELECT_PUBLIC_HEARING_IDS (
        "SELECT id FROM ${schema}." + SqlTable.PUBLIC_HEARING
    ),
    // TODO: no unique constraint on filename
    SELECT_HEARING_ID_BY_FILENAME (
            "SELECT id FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
                    "WHERE filename = :filename"
    ),
    SELECT_PUBLIC_HEARING_BY_ID (
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "WHERE id = :id"
    ),
    UPDATE_PUBLIC_HEARING (
        "UPDATE ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "SET title = :title, date = :date, address = :address, text = :text, " +
        "start_time = :startTime, end_time = :endTime, " +
        "modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime" + "\n" +
        "WHERE id = :id"
    ),
    INSERT_PUBLIC_HEARING (
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "(filename, date, title, address, text, start_time, end_time)" + "\n" +
        "VALUES (:filename, :date, :title, :address, :text, :startTime, :endTime)"
    ),
    SELECT_PUBLIC_HEARING_UPDATES (
            "SELECT filename, modified_date_time, COUNT(*) OVER() as total_updated " +
            "FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
            "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime"
    );

    private final String sql;

    SqlPublicHearingQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
