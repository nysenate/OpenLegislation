package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillUpdatesQuery implements BasicSqlQuery
{
    SELECT_BILL_UPDATES_FRAGMENT(
        "SELECT bill_print_no, bill_session_year,\n" +
        "       %s \n" + // Any additional columns are replaced here
        "FROM ${schema}." + SqlTable.BILL_CHANGE_LOG + " log\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "%s\n" + // Additional WHERE clause
        "AND (${updateFieldFilter}) \n" + // Update field filter gets replaced based on method args
        "%s"),  // GROUP BY clause if necessary

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "sobi_fragment_id AS last_fragment_id, action_date_time AS last_processed_date_time, \n" +
        "published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action, hstore_to_array(data) AS data\n"
    ),

    SELECT_BILL_UPDATE_TOKENS(
        String.format(SELECT_BILL_UPDATES_FRAGMENT.sql,
            // Select columns
            "MAX(sobi_fragment_id) AS last_fragment_id, MAX(action_date_time) AS last_processed_date_time, \n" +
            "MAX(published_date_time) AS last_published_date_time, COUNT(*) OVER () AS total_updated\n",
            // No extra where clause
            "",
            // Group by bill ids for update tokens
            "GROUP BY bill_print_no, bill_session_year")
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
            "AND bill_print_no = :printNo AND bill_session_year = :session\n",
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