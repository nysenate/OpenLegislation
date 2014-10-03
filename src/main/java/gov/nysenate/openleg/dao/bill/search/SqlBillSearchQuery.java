package gov.nysenate.openleg.dao.bill.search;

import gov.nysenate.openleg.dao.base.SearchSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlBillSearchQuery implements SearchSqlQuery
{
    /** FIXME: http://dev.nysenate.gov/issues/8154 */

    /** Refreshes the data on the bill search aggregate table for the specified bill */
    REFRESH_BILL_SEARCH("SELECT ${search_schema}.refresh_bill_search(:printNo::text, :sessionYear::smallint)"),

    /** Template that wraps the search into a sub query to produce a single result for a given base bill id.
     *  The rank for a bill is determined by taking the average of the ranks of all it's amendments. */
    SEARCH_BASE_BILL_WRAPPER(
        "SELECT bill_print_no, bill_session_year, AVG(rank) AS rank FROM (%s) k\n" +
        "GROUP BY bill_print_no, bill_session_year"
    ),

    /** Used as a template for performing queries against the bill search aggregate table. */
    SELECT_GLOBAL(
        "SELECT %s\n" +
        "FROM ${search_schema}." + SqlTable.BILL_FULL_SEARCH + ", plainto_tsquery(:query) AS query\n" +
        "WHERE search @@ query"
    ),

    /** Count the total number of global matches, counting each matched amendment as a single result. */
    COUNT_GLOBAL_AMEND_VERSION(
        String.format(SELECT_GLOBAL.sql, "COUNT(*)")
    ),

    /** Perform a ranked search across all bills and all available bill fields, returning a separate row for each matched
     *  bill amendment. */
    SEARCH_GLOBAL_AMEND_VERSION(
        String.format(SELECT_GLOBAL.sql,
            "print_no AS bill_print_no, session_year AS bill_session_year, version, 1 AS rank") //ts_rank(search, query) AS rank")
    ),

    /** Count the total number of global matches, counting each matched bill just once, regardless of amendment count. */
    COUNT_GLOBAL_BASE_BILL(
        String.format(SELECT_GLOBAL.sql, "COUNT (*)")
    ),

    /** Perform a ranked search across all bills, returning a single row for any matched base bill. */
    SEARCH_GLOBAL_BASE_BILL(
        String.format(SEARCH_BASE_BILL_WRAPPER.sql, SEARCH_GLOBAL_AMEND_VERSION.sql)
    ),

    /** Used as a template to dynamically construct advanced bill search queries. */
    SELECT_ADVANCED_BASE(
        "SELECT %s\n" +
        "FROM ${search_schema}." + SqlTable.BILL_INFO_SEARCH + " t1\n" +
        "JOIN ${search_schema}." + SqlTable.BILL_AMENDMENT_SEARCH + " t2\n" +
        "     ON t1.print_no = t2.bill_print_no AND t1.session_year = t2.bill_session_year\n" +
        "${additional_joins}\n" +
        "${queries_declaration}\n" +   // Don't forget to add a leading comma when replacing the string.
        "WHERE ${filter_clause}\n"
    ),

    /** Count the total number of results, counting each matched amendment as a single result. */
    COUNT_ADVANCED_AMEND_VERSION(
        String.format(SELECT_ADVANCED_BASE.sql, "COUNT(*)")
    ),

    /** Perform a ranked search across all bills, returning a separate row for each matched bill amendment. */
    SEARCH_ADVANCED_AMEND_VERSION(
        String.format(SELECT_ADVANCED_BASE.sql,
            "t2.bill_print_no, t2.bill_session_year, t2.version, ts_rank(${vectors}, ${queries}) AS rank")
    ),

    /** Count the total number of results, counting each matched bill just once, regardless of amendment count. */
    COUNT_ADVANCED_BASE_BILL(
        String.format(SELECT_ADVANCED_BASE.sql, "COUNT(DISTINCT (t2.bill_print_no, t2.bill_session_year))")
    ),

    /** Perform a ranked search across all bills, returning a single row for any matched base bill. */
    SEARCH_ADVANCED_BASE_BILL(
        String.format(SEARCH_BASE_BILL_WRAPPER.sql, SEARCH_ADVANCED_AMEND_VERSION.sql)
    ),

    ;

    String sql;

    SqlBillSearchQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
