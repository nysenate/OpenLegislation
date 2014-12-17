package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillUpdatesQuery implements BasicSqlQuery
{
    SELECT_BILLS_UPDATED_DURING(
        "SELECT key->'bill_print_no' AS bill_print_no, key->'bill_session_year' AS bill_session_year," +
        "       MAX(action_date_time) AS last_update_date_time, COUNT(*) OVER () AS total_updated \n" +
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + "\n" +
        "WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "AND defined(key, 'bill_print_no') AND defined(key, 'bill_session_year')\n" +
        "AND (${updateFieldFilter}) \n" +
        "GROUP BY key->'bill_print_no', key->'bill_session_year'"
    ),

    SELECT_UPDATES_FOR_BILL(
        "SELECT table_name, action, hstore_to_array(key) AS key, hstore_to_array(data) AS data, action_date_time, \n" +
        "       sobi_fragment_id \n" +
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + "\n" +
        "WHERE action_date_time BETWEEN :startDateTime AND :endDateTime \n" +
        "AND key @> hstore(ARRAY['bill_print_no', 'bill_session_year'], ARRAY[:printNo, :session::text])\n" +
        "AND (${updateFieldFilter})"
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
