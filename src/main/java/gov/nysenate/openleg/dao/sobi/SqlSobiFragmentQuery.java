package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SqlQueryEnum;
import gov.nysenate.openleg.dao.base.SqlQueryUtils;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlSobiFragmentQuery implements SqlQueryEnum
{
    CHECK_SOBI_FRAGMENT_EXISTS_SQL(
        "SELECT 1 FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    GET_SOBI_FRAGMENT_BY_FILE_NAME_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE sobi_fragment_type = :sobiFragmentType AND fragment_id = :fileName"
    ),
    GET_SOBI_FRAGMENTS_BY_SOBI_FILE_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + " WHERE sobi_file_name = :sobiFileName"
    ),
    GET_SOBI_FRAGMENTS_BY_SOBI_FILE_AND_TYPE_SQL(
        "SELECT * FROM ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "WHERE sobi_file_name = :sobiFileName AND sobi_fragment_type = :sobiFragmentType"
    ),
    UPDATE_SOBI_FRAGMENT_SQL(
        "UPDATE ${schema}." + SqlTable.SOBI_FRAGMENT + "\n" +
        "SET sobi_file_name = :sobiFileName, published_date_time = :publishedDateTime, " +
        "    sobi_fragment_type = :sobiFragmentType, file_counter = :fileCounter, text = :text\n" +
        "WHERE fragment_id = :fragmentId"
    ),
    INSERT_SOBI_FRAGMENT_SQL(
        "INSERT INTO ${schema}." + SqlTable.SOBI_FRAGMENT +
        " (sobi_file_name, fragment_id, published_date_time, sobi_fragment_type, file_counter, text)" +
        " VALUES (:sobiFileName, :fragmentId, :publishedDateTime, :sobiFragmentType, :fileCounter, :text)"
    ),
    DELETE_SOBI_FRAGMENTS_SQL(
        "DELETE FROM ${schema}." + SqlTable.SOBI_FRAGMENT + " WHERE sobi_file_name = :sobiFileName"
    );

    private String sql;

    SqlSobiFragmentQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String environmentSchema) {
        return SqlQueryUtils.getSqlWithSchema(this.sql, environmentSchema);
    }
}
