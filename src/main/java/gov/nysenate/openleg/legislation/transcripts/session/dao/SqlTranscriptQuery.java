package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlTranscriptQuery implements BasicSqlQuery
{
    SELECT_TRANSCRIPT_IDS_BY_YEAR(
        "SELECT date_time FROM ${schema}." + SqlTable.TRANSCRIPT
    ),
    SELECT_TRANSCRIPT_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE date_time = :dateTime"
    ),
    UPDATE_TRANSCRIPT(
        "UPDATE ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "SET session_type = :sessionType, transcript_filename = :transcriptFilename, location = :location, text = :text,  modified_date_time = :modified_date_time\n" +
        "WHERE date_time = :dateTime"
    ),
    INSERT_TRANSCRIPT(
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "(transcript_filename, session_type, date_time, location, text)\n" +
        "VALUES (:transcriptFilename, :sessionType, :dateTime, :location, :text)"
    ),
    SELECT_TRANSCRIPTS_UPDATED_DURING(
        "SELECT date_time, modified_date_time, COUNT(*) OVER() as total_updated " +
        "FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime"
    );

    private String sql;

    SqlTranscriptQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
