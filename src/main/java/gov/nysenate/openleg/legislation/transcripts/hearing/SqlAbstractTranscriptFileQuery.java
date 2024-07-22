package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlAbstractTranscriptFileQuery implements BasicSqlQuery {
    GET_PENDING_TRANSCRIPT_FILES ("""
        SELECT * FROM ${schema}.%s
        WHERE pending_processing = true"""
    ),
    INSERT_TRANSCRIPT_FILE ("""
        INSERT INTO ${schema}.%s
            (filename, processed_date_time, processed_count,
            pending_processing, archived)
        VALUES (:filename, :processedDateTime, :processedCount,
            :pendingProcessing, :archived)"""
    ),
    UPDATE_TRANSCRIPT_FILE ("""
        UPDATE ${schema}.%s
        SET processed_date_time = :processedDateTime,
            processed_count = :processedCount,
            pending_processing = :pendingProcessing,
            archived = :archived
        WHERE filename = :filename"""
    ),
    RENAME_TRANSCRIPT_FILE("""
        UPDATE ${schema}.%s
        SET filename = :originalName
        WHERE filename = :newName""");

    private final String sql;

    SqlAbstractTranscriptFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    public String getSql(String schema, boolean isHearing) {
        return getSql(schema).formatted(isHearing ? SqlTable.HEARING_FILE : SqlTable.TRANSCRIPT_FILE);
    }
}
