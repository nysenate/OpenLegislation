package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlTranscriptQuery implements BasicSqlQuery
{
    SELECT_TRANSCRIPT_IDS_BY_YEAR(
        "SELECT session_type, date_time FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE EXTRACT(YEAR FROM date_time) = :year"
    ),
    SELECT_TRANSCRIPT_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE session_type = :sessionType AND date_time = :dateTime"
    ),
    UPDATE_TRANSCRIPT(
        "UPDATE ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "SET location = :location, text = :text, transcript_file = :transcriptFile \n" +
        "WHERE session_type = :sessionType AND date_time = :dateTime"
    ),
    INSERT_TRANSCRIPT(
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "(session_type, date_time, location, text, transcript_file)\n" +
        "VALUES (:sessionType, :dateTime, :location, :text, :transcriptFile)"
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
