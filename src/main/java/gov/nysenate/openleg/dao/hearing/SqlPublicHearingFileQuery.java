package gov.nysenate.openleg.dao.hearing;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlPublicHearingFileQuery implements BasicSqlQuery
{
    SELECT_PENDING_PUBLIC_HEARING_FILES(
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING_FILE + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_PUBLIC_HEARING_FILE(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING_FILE + "\n" +
        "(filename,  processed_date_time, processed_count," +
        "pending_processing, archived)" +  "\n" +
        "VALUES (:fileName, :processedDateTime, :processedCount," +
        ":pendingProcessing, :archived)"
    ),
    UPDATE_PUBLIC_HEARING_FILE(
        "UPDATE ${schema}." + SqlTable.PUBLIC_HEARING_FILE + "\n" +
        "SET processed_date_time = :processedDateTime," +
        "    processed_count = :processedCount," +
        "    pending_processing = :pendingProcessing," +
        "    archived = :archived " +
        "WHERE filename = :fileName"
    );

    private String sql;

    SqlPublicHearingFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
