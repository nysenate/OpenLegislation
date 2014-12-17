package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlTranscriptUpdatesQuery implements BasicSqlQuery
{
    SELECT_TRANSCRIPTS_UPDATED_DURING(
        "SELECT transcript_filename, modified_date_time FROM ${schema}." + SqlTable.TRANSCRIPT + "\n" +
        "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime"
    );

    private String sql;

    SqlTranscriptUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
