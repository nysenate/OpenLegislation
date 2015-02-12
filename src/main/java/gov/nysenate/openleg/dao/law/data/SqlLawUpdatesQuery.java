package gov.nysenate.openleg.dao.law.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLawUpdatesQuery implements BasicSqlQuery
{
    SELECT_LAW_UPDATES_FRAGMENT(
        "SELECT %s\n" +
        "FROM ${schema}." + SqlTable.LAW_CHANGE_LOG + "\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "AND table_name = '" + SqlTable.LAW_DOCUMENT + "'\n" +
        "%s\n" + // Additional WHERE clause
        "%s"     // GROUP BY clause if necessary
    ),

    SELECT_COLUMNS_FOR_TOKEN_FRAGMENT(
        "MAX(action_date_time) AS last_processed_date_time, MAX(law_file_name) AS last_source_file,\n" +
        "MAX(published_date_time) AS last_published_date_time, COUNT(*) OVER () AS total_updated"
    ),

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "law_file_name AS last_source_file, action_date_time AS last_processed_date_time, \n" +
        "published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action\n"
    ),

    SELECT_LAW_UPDATE_TOKENS(
        String.format(SELECT_LAW_UPDATES_FRAGMENT.sql,
            "law_id, " + SELECT_COLUMNS_FOR_TOKEN_FRAGMENT.sql,
            "",
            "GROUP BY law_id")
    ),

    SELECT_LAW_UPDATE_DIGESTS(
        String.format(SELECT_LAW_UPDATES_FRAGMENT.sql,
            "law_id, document_id, " + SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            "",
            "")
    ),

    SELECT_LAW_UPDATE_DIGESTS_FOR_LAW(
        String.format(SELECT_LAW_UPDATES_FRAGMENT.sql,
            "law_id, document_id, " + SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            "AND law_id = :lawId",
            "")
    ),

    SELECT_LAW_UPDATE_DIGESTS_FOR_DOCUMENT(
        String.format(SELECT_LAW_UPDATES_FRAGMENT.sql,
            "law_id, document_id, " + SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            "AND document_id = :documentId",
            "")
    );

    private String sql;

    SqlLawUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
