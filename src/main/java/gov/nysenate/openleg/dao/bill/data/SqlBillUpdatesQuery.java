package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillUpdatesQuery implements BasicSqlQuery
{
    SELECT_BILL_UPDATES_FRAGMENT(
        "SELECT key->'bill_print_no' AS bill_print_no, key->'bill_session_year' AS bill_session_year,\n" +
        "       %s \n" + // Any additional columns are replaced here
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + " log\n" +
        "LEFT JOIN ${schema}." + SqlTable.SOBI_FRAGMENT + " sobi\n" +
        "     ON log.sobi_fragment_id = sobi.fragment_id\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "AND defined(key, 'bill_print_no') AND defined(key, 'bill_session_year')\n" +
        "%s\n" + // Any addtional where clause
        "AND (${updateFieldFilter}) \n" + // Update field filter gets replaced based on method args
        "%s"),

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "sobi.fragment_id AS last_fragment_id, log.action_date_time AS last_processed_date_time, \n" +
        "sobi.published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action, hstore_to_array(key) AS key, hstore_to_array(data) AS data\n"
    ),

    SELECT_BILL_UPDATE_TOKENS(
        String.format(SELECT_BILL_UPDATES_FRAGMENT.sql,
            // Select columns
            "MAX(sobi.fragment_id) AS last_fragment_id, MAX(log.action_date_time) AS last_processed_date_time, \n" +
            "MAX(sobi.published_date_time) AS last_published_date_time, COUNT(*) OVER () AS total_updated\n",
            // No extra where clause
            "",
            // Group by bill ids for update tokens
            "GROUP BY key->'bill_print_no', key->'bill_session_year'")
    ),

    SELECT_BILL_UPDATE_DIGESTS(
        String.format(SELECT_BILL_UPDATES_FRAGMENT.sql,
            // Select columns
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            // No extra where clause
            "",
            // No group by needed for digests due to pagination/performance issues
            "")
    ),

    SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_BILL(
        String.format(SELECT_BILL_UPDATES_FRAGMENT.sql,
            // Select columns
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            // No extra where clause
            "AND key @> hstore(ARRAY['bill_print_no', 'bill_session_year'], ARRAY[:printNo, :session::text])\n",
            // No group by needed for digests due to pagination/performance issues
            "")
    );

    private String sql;

    SqlBillUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}