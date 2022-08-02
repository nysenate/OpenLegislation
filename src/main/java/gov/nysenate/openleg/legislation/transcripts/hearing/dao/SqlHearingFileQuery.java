package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlHearingFileQuery implements BasicSqlQuery {
    SELECT_PENDING_PUBLIC_HEARING_FILES(
        "SELECT * FROM ${schema}." + SqlTable.HEARING_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_PUBLIC_HEARING_FILE(
        "INSERT INTO ${schema}." + SqlTable.HEARING_FILE + "\n" +
        "(filename,  processed_date_time, processed_count," +
        "pending_processing, archived)" +  "\n" +
        "VALUES (:fileName, :processedDateTime, :processedCount," +
        ":pendingProcessing, :archived)"
    ),
    UPDATE_PUBLIC_HEARING_FILE(
        "UPDATE ${schema}." + SqlTable.HEARING_FILE + "\n" +
        "SET processed_date_time = :processedDateTime," +
        "    processed_count = :processedCount," +
        "    pending_processing = :pendingProcessing," +
        "    archived = :archived " +
        "WHERE filename = :fileName"
    );

    private final String sql;

    SqlHearingFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
