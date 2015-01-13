package gov.nysenate.openleg.dao.calendar.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCalendarUpdatesQuery implements BasicSqlQuery
{
    SELECT_CALENDAR_UPDATES_FRAGMENT(
        "SELECT calendar_no, calendar_year, %s\n" +
        "FROM ${schema}." + SqlTable.CALENDAR_CHANGE_LOG + "\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "%s\n" + // Additional WHERE clause
        "%s" // GROUP BY clause if necessary
    ),

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "sobi_fragment_id AS last_fragment_id, action_date_time AS last_processed_date_time, \n" +
        "published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action, hstore_to_array(data) AS data\n"
    ),

    SELECT_CALENDAR_UPDATE_TOKENS(
        String.format(SELECT_CALENDAR_UPDATES_FRAGMENT.sql,
        // Select columns
            "       MAX(action_date_time) AS last_processed_date_time, MAX(sobi_fragment_id) AS last_fragment_id,\n" +
            "       MAX(published_date_time) AS last_published_date_time, COUNT(*) OVER() AS total_updated\n",
        // No extra where clause
        "",
        // Group by calendar id
        "GROUP BY calendar_no, calendar_year")
    ),

    SELECT_CALENDAR_UPDATE_DIGESTS(
        String.format(SELECT_CALENDAR_UPDATES_FRAGMENT.sql,
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            "", "")
    ),

    SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_CALENDAR(
        String.format(SELECT_CALENDAR_UPDATES_FRAGMENT.sql,
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            "AND calendar_no = :calendarNo AND calendar_year = :calendarYear",
            "")
    );

    private String sql;

    SqlCalendarUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
