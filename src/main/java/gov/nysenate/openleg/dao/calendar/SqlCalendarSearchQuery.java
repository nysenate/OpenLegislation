package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCalendarSearchQuery implements BasicSqlQuery
{
    /** --- Calendar Type prefixes --- */

    ACTIVE_LIST_PREFIX("al"),
    SUPPLEMENTAL_PREFIX("sup"),

    /** --- Sub Queries --- */

    WITH("WITH"),

    BILL_PRINT_NO_SUBQUERY(
        "\n%1$s_bills_query AS (" + "\n" +
        "\tSELECT %2$s, array_agg(bill_print_no::varchar) AS %1$s_bills" + "\n" +
        "\tFROM ${schema}.%3$s" + "\n" +
        "\tWHERE bill_session_year = :billSessionYear AND bill_print_no IN (:allBills)" + "\n" +
        "\tGROUP BY %2$s " + "\n)"
    ),
    ACTIVE_LIST_BILL_PRINT_NO_SUBQUERY(
        String.format(BILL_PRINT_NO_SUBQUERY.getSql(),
            ACTIVE_LIST_PREFIX.toString(), "calendar_active_list_id", SqlTable.CALENDAR_ACTIVE_LIST_ENTRY.toString())
    ),
    SUPPLEMENTAL_BILL_PRINT_NO_SUBQUERY(
        String.format(BILL_PRINT_NO_SUBQUERY.getSql(),
            SUPPLEMENTAL_PREFIX.toString(), "calendar_sup_id", SqlTable.CALENDAR_SUP_ENTRY.toString())
    ),
    CALENDAR_NO_SUBQUERY(
        "\n%1$s_bcns_query AS (\n" +
        "\tSELECT %2$s, array_agg(bill_calendar_no::integer) AS %1$s_bcns" + "\n" +
        "\tFROM ${schema}.%3$s" + "\n" +
        "\tWHERE bill_calendar_no IN (:allBillCalNos)" + "\n" +
        "\tGROUP BY %2$s " + "\n)"
    ),
    ACTIVE_LIST_CALENDAR_NO_SUBQUERY(
        String.format(CALENDAR_NO_SUBQUERY.getSql(),
            ACTIVE_LIST_PREFIX.toString(), "calendar_active_list_id", SqlTable.CALENDAR_ACTIVE_LIST_ENTRY.toString())
    ),
    SUPPLEMENTAL_CALENDAR_NO_SUBQUERY(
        String.format(CALENDAR_NO_SUBQUERY.getSql(),
            SUPPLEMENTAL_PREFIX.toString(), "calendar_sup_id", SqlTable.CALENDAR_SUP_ENTRY.toString())
    ),
    SECTION_CODE_SUBQUERY(
        "\nsup_scode_query AS (\n" +
        "\tSELECT calendar_sup_id, array_agg(section_code::integer) AS sup_codes" + "\n" +
        "\tFROM ${schema}." + SqlTable.CALENDAR_SUP_ENTRY + "\n" +
        "\tWHERE section_code IN (:allSectionCodes)" + "\n" +
        "\tGROUP BY calendar_sup_id " + "\n)"
    ),

    /** --- Calendar Type Sub Query Selectors --- */

    CALTYPE_SUBQUERY_BASE_SELECT(
        "\tSELECT %1$s.calendar_year AS cal_year, %1$s.calendar_no AS cal_num"
    ),
    ACTIVE_LIST_BASE_SELECT(
        String.format(CALTYPE_SUBQUERY_BASE_SELECT.getSql(), ACTIVE_LIST_PREFIX.getSql())
    ),
    SUPPLEMENTAL_BASE_SELECT(
        String.format(CALTYPE_SUBQUERY_BASE_SELECT.getSql(), SUPPLEMENTAL_PREFIX.getSql())
    ),
    CALTYPE_SUBQUERY_DATE_SELECTOR(
        ", %1$s.calendar_date AS cal_date"
    ),
    ACTIVE_LIST_DATE_SELECTOR(
        String.format(CALTYPE_SUBQUERY_DATE_SELECTOR.getSql(), ACTIVE_LIST_PREFIX.getSql())
    ),
    SUPPLEMENTAL_DATE_SELECTOR(
        String.format(CALTYPE_SUBQUERY_DATE_SELECTOR.getSql(), SUPPLEMENTAL_PREFIX.getSql())
    ),
    CALTYPE_SUBQUERY_PRINT_NO_SELECTOR(
        ", %1$s_bills AS bills"
    ),
    ACTIVE_LIST_PRINT_NO_SELECTOR(
        String.format(CALTYPE_SUBQUERY_PRINT_NO_SELECTOR.getSql(), ACTIVE_LIST_PREFIX.getSql())
    ),
    SUPPLEMENTAL_PRINT_NO_SELECTOR(
        String.format(CALTYPE_SUBQUERY_PRINT_NO_SELECTOR.getSql(), SUPPLEMENTAL_PREFIX.getSql())
    ),
    CALTYPE_SUBQUERY_BILL_CALENDAR_NO_SELECTOR(
        ", %1$s_bcns AS bcns"
    ),
    ACTIVE_LIST_BILL_CALENDAR_NO_SELECTOR(
        String.format(CALTYPE_SUBQUERY_BILL_CALENDAR_NO_SELECTOR.getSql(), ACTIVE_LIST_PREFIX.getSql())
    ),
    SUPPLEMENTAL_BILL_CALENDAR_NO_SELECTOR(
        String.format(CALTYPE_SUBQUERY_BILL_CALENDAR_NO_SELECTOR.getSql(), SUPPLEMENTAL_PREFIX.getSql())
    ),
    ACTIVE_LIST_SEQUENCE_NO_SELECTOR(
        ", al.sequence_no AS al_sequence_no"
    ),
    SUPPLEMENTAL_VERSION_SELECTOR(
        ", sup.sup_version AS sup_version"
    ),
    SUPPLEMENTAL_SECTION_CODE_SELECTOR(
        ", sup_codes"
    ),

    /** --- Calendar Type Sub Query Table Sources --- */

    CALTYPE_SUBQUERY_FROM(
            "\n\t" + "FROM ${schema}.%1$s AS %2$s"
    ),
    ACTIVE_LIST_FROM(
        String.format(CALTYPE_SUBQUERY_FROM.getSql(), SqlTable.CALENDAR_ACTIVE_LIST.toString(), ACTIVE_LIST_PREFIX.getSql())
    ),
    SUPPLEMENTAL_FROM(
        String.format(CALTYPE_SUBQUERY_FROM.getSql(), SqlTable.CALENDAR_SUPPLEMENTAL.toString(), SUPPLEMENTAL_PREFIX.getSql())
    ),
    CALTYPE_SUBQUERY_JOIN(
        "\n\t" + "JOIN %1$s_%2$s_query ON %1$s_%2$s_query.calendar_%3$s_id = %1$s.id"
    ),
    ACTIVE_LIST_PRINT_NO_JOIN(
        String.format(CALTYPE_SUBQUERY_JOIN.getSql(), ACTIVE_LIST_PREFIX.getSql(), "bills", "active_list")
    ),
    ACTIVE_LIST_BILL_CALENDAR_NO_JOIN(
        String.format(CALTYPE_SUBQUERY_JOIN.getSql(), ACTIVE_LIST_PREFIX.getSql(), "bcns", "active_list")
    ),
    SUPPLEMENTAL_PRINT_NO_JOIN(
        String.format(CALTYPE_SUBQUERY_JOIN.getSql(), SUPPLEMENTAL_PREFIX.getSql(), "bills", "sup")
    ),
    SUPPLEMENTAL_BILL_CALENDAR_NO_JOIN(
        String.format(CALTYPE_SUBQUERY_JOIN.getSql(), SUPPLEMENTAL_PREFIX.getSql(), "bcns", "sup")
    ),
    SUPPLEMENTAL_BILL_SECTION_CODE_JOIN(
        String.format(CALTYPE_SUBQUERY_JOIN.getSql(), SUPPLEMENTAL_PREFIX.getSql(), "scode", "sup")
    ),

    /** --- Selectors --- */

    SELECT_BASE(
        "SELECT DISTINCT cal_year AS year, cal_num AS calendar_no"
    ),
    SELECT_ACTIVE_LIST(
        SELECT_BASE.getSql() + ", al_sequence_no"
    ),
    SELECT_SUPPLEMENTAL(
        SELECT_BASE.getSql() + ", sup_version"
    ),
    SELECT_COUNT(
        "SELECT COUNT(*) FROM (%s) as sq"
    ),

    /** --- Table sources --- */

    FROM_SINGLE_SOURCE(
        "\n" + "FROM (%s) AS q"
    ),
    FROM_UNION(
        "\n" + "FROM (" + "\n" +
        "(\n%1$s\n)" + "\n" +
        "UNION" + "\n" +
        "(\n%2$s\n)" + "\n" +
        ") AS q"
    ),

    /** --- Where clauses --- */

    WHERE("\n" + "WHERE "),
    AND("AND "),
    OR("OR "),

    WHERE_YEAR(
        "cal_year = :year "
    ),
    WHERE_DATE_RANGE(
        "cal_date BETWEEN :dateRangeStart AND :dateRangeEnd "
    ),
    WHERE_PRINT_NOS(
        "bills @> ARRAY[ :billSet%1$d ] "
    ),
    WHERE_BILL_CAL_NOS(
        "bcns @> ARRAY[ :calNoSet%1$d ] "
    ),
    WHERE_SECTION_CODE(
        "sup_codes @> ARRAY[ :codeSet%1$d ] "
    )
    ;

    private String sql;
    
    private SqlCalendarSearchQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}
