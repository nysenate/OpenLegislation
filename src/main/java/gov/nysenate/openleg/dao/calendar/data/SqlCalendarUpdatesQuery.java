package gov.nysenate.openleg.dao.calendar.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;
import gov.nysenate.openleg.model.sobi.SobiFragment;

public enum SqlCalendarUpdatesQuery implements BasicSqlQuery
{
    SELECT_CALENDAR_UPDATED_DURING(
        "SELECT key->'calendar_no' AS calendar_no, key->'calendar_year' AS calendar_year,\n" +
        "   MAX(action_date_time) AS action_date_time, MAX(sobi.fragment_id) AS fragment_id, " +
        "   MAX(sobi.published_date_time) AS published_date_time, COUNT(*) OVER() AS total_updated\n" +
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + " log\n" +
        "LEFT JOIN ${schema}." + SqlTable.SOBI_FRAGMENT + " sobi\n" +
        "   ON log.sobi_fragment_id = sobi.fragment_id\n" +
        "WHERE ${date_column} BETWEEN :startDateTime AND :endDateTime\n" +
        "   AND defined(key, 'calendar_no') AND defined(key, 'calendar_year')\n" +
        "GROUP BY calendar_no, calendar_year"
    ),
    SELECT_UPDATE_DIGESTS(
        "SELECT key->'calendar_no' AS calendar_no, key->'calendar_year' AS calendar_year,\n" +
        "   action, action_date_time, published_date_time, fragment_id, table_name,\n" +
        "   hstore_to_array(data) AS data, hstore_to_array(key) AS key, COUNT(*) OVER() AS total_updated\n" +
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + " log\n" +
        "LEFT JOIN ${schema}." + SqlTable.SOBI_FRAGMENT + " sobi\n" +
        "   ON log.sobi_fragment_id = sobi.fragment_id\n" +
        "WHERE ${date_column} BETWEEN :startDateTime AND :endDateTime\n" +
        "   AND defined(key, 'calendar_no') AND defined(key, 'calendar_year')\n"
    ),
    SELECT_UPDATE_DIGESTS_FOR_CALENDAR(
        SELECT_UPDATE_DIGESTS.sql +
        "   AND key->'calendar_no' = :calendarNo::text AND key->'calendar_year' = :calendarYear::text\n"
    )
    ;

    private String sql;

    SqlCalendarUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
