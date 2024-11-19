package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlTranscriptQuery implements BasicSqlQuery {
    SELECT_TRANSCRIPT_IDS_BY_YEAR (
        "SELECT date_time, session_type FROM ${schema}." + SqlTable.TRANSCRIPT
    ),
    SELECT_TRANSCRIPT_BY_DATE_TIME (
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE date_time = :dateTime"),
    SELECT_TRANSCRIPT_BY_ID (
        SELECT_TRANSCRIPT_BY_DATE_TIME.sql + " AND session_type ILIKE :sessionType"
    ),
    UPDATE_TRANSCRIPT (
        "UPDATE ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "SET day_type = :dayType, location = :location, text = :text, modified_date_time = :modified_date_time, transcript_filename = :transcriptFilename\n" +
        "WHERE date_time = :dateTime AND session_type = :sessionType"
    ),
    INSERT_TRANSCRIPT (
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "(date_time, session_type, day_type, location, text, transcript_filename)\n" +
        "VALUES (:dateTime, :sessionType, :dayType, :location, :text, :transcriptFilename)"
    ),
    SELECT_TRANSCRIPTS_UPDATED_DURING (
        "SELECT date_time, session_type, modified_date_time, COUNT(*) OVER() as total_updated " +
        "FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime"
    );

    private final String sql;

    SqlTranscriptQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
