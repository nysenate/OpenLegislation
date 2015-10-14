package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlTranscriptQuery implements BasicSqlQuery
{
    SELECT_TRANSCRIPT_IDS_BY_YEAR(
        "SELECT transcript_filename FROM ${schema}." + SqlTable.TRANSCRIPT
    ),
    SELECT_TRANSCRIPT_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE transcript_filename = :transcriptFilename"
    ),
    UPDATE_TRANSCRIPT(
        "UPDATE ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "SET session_type = :sessionType, date_time = :dateTime, location = :location, text = :text \n" +
        "WHERE transcript_filename = :transcriptFilename"
    ),
    INSERT_TRANSCRIPT(
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "(transcript_filename, session_type, date_time, location, text)\n" +
        "VALUES (:transcriptFilename, :sessionType, :dateTime, :location, :text)"
    ),
    SELECT_TRANSCRIPTS_UPDATED_DURING(
        "SELECT transcript_filename, modified_date_time, COUNT(*) OVER() as total_updated " +
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
