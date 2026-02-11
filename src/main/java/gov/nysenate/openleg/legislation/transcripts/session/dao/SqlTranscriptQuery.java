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
    SELECT_TRANSCRIPTS_AND_BILLS (
        "SELECT * FROM ${schema}." + SqlTable.TRANSCRIPT + " AS t\n" +
        "LEFT JOIN ${schema}." + SqlTable.TRANSCRIPT_BILLS + " AS b\n" +
        "ON t.session_type = b.session_type AND t.date_time = b.date_time\n" +
        "WHERE t.date_time = :dateTime AND t.session_type ILIKE :sessionType"
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
    INSERT_TRANSCRIPT_BILL_IDS (
        "INSERT INTO ${schema}." + SqlTable.TRANSCRIPT_BILLS + "\n" +
        "(session_type, date_time, bill_print_no, bill_session_year)\n" +
        "SELECT :sessionType, :dateTime, :billPrintNo, :billSessionYear\n" +
        "WHERE EXISTS (\n" +
        "   SELECT 1\n" +
        "   FROM ${schema}." + SqlTable.BILL + "\n" +
        "   WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear)"
    ),
    DELETE_TRANSCRIPT_BILL_IDS (
        "DELETE FROM ${schema}." + SqlTable.TRANSCRIPT_BILLS + "\n" +
        "WHERE session_type = :sessionType AND date_time = :dateTime AND bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear"
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
