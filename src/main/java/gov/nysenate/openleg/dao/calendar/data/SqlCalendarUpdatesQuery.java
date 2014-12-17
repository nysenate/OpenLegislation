package gov.nysenate.openleg.dao.calendar.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCalendarUpdatesQuery implements BasicSqlQuery {
    SELECT_CALENDAR_UPDATED_DURING(
        "SELECT calendar_no, calendar_year, MAX(action_date_time) AS latest_action_date_time, COUNT(*) OVER() AS total_updated\n" +
        "FROM (\n" +
        "   SELECT key->'calendar_no' AS calendar_no, key->'calendar_year' AS calendar_year, action_date_time\n" +
        "       FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + "\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(key, 'calendar_no') AND defined(key, 'calendar_year')\n" +
        "   UNION\n" +
        "   SELECT al.calendar_no::text AS calendar_no, al.calendar_year::text AS calendar_year, action_date_time\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_ACTIVE_LIST +" al\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON al.id::text = ent.key->'calendar_active_list_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(ent.key, 'calendar_active_list_id')\n" +
        "   UNION\n" +
        "   SELECT sup.calendar_no::text AS calendar_no, sup.calendar_year::text AS calendar_year, action_date_time\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_SUPPLEMENTAL + " sup\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON sup.id::text = ent.key->'calendar_sup_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime AND defined(ent.key, 'calendar_sup_id')\n" +
        ") AS calendar_updates\n" +
        "GROUP BY calendar_no, calendar_year"
    ),
    SELECT_UPDATES_FOR_CALENDAR(
        "SELECT * FROM (\n" +
        "   SELECT key->'calendar_no' AS calendar_no, key->'calendar_year' AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name,\n" +
        "           hstore_to_array(data) AS data, hstore_to_array(key) AS key\n" +
        "       FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + "\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(key, 'calendar_no') AND defined(key, 'calendar_year')\n" +
        "           AND key->'calendar_no' = :calendarNo::text AND key->'calendar_year' = :calendarYear::text\n" +
        "   UNION\n" +
        "   SELECT al.calendar_no::text AS calendar_no, al.calendar_year::text AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name, hstore_to_array(data) AS data,\n" +
        "           hstore_to_array(key || hstore(\n" +
        "               ARRAY['calendar_no', 'calendar_year'],\n" +
        "               ARRAY[al.calendar_no::text, al.calendar_year::text])) AS key\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_ACTIVE_LIST +" al\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON al.id::text = ent.key->'calendar_active_list_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(ent.key, 'calendar_active_list_id')\n" +
        "           AND calendar_no = :calendarNo AND calendar_year = :calendarYear\n" +
        "   UNION\n" +
        "   SELECT sup.calendar_no::text AS calendar_no, sup.calendar_year::text AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name, hstore_to_array(data) AS data,\n" +
        "           hstore_to_array(key || hstore(" +
        "               ARRAY['calendar_no', 'calendar_year'],\n" +
        "               ARRAY[sup.calendar_no::text, sup.calendar_year::text])) AS key\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_SUPPLEMENTAL + " sup\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON sup.id::text = ent.key->'calendar_sup_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime AND defined(ent.key, 'calendar_sup_id')\n" +
        "           AND calendar_no = :calendarNo AND calendar_year = :calendarYear\n" +
        ") AS calendar_update_digest"
    ),
    SELECT_UPDATE_DIGESTS_DURING(
        "SELECT * FROM\n" +
        "(\n" +
        SELECT_CALENDAR_UPDATED_DURING.sql + "\n" +
        "ORDER BY latest_action_date_time ${sort_order}\n" +
        "LIMIT ${limit} OFFSET ${offset}\n" +
        ") AS token\n" +
        "JOIN\n" +
        "(\n" +
        "   SELECT key->'calendar_no' AS calendar_no, key->'calendar_year' AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name,\n" +
        "           hstore_to_array(data) AS data, hstore_to_array(key) AS key\n" +
        "       FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + "\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(key, 'calendar_no') AND defined(key, 'calendar_year')\n" +
        "   UNION\n" +
        "   SELECT al.calendar_no::text AS calendar_no, al.calendar_year::text AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name, hstore_to_array(data) AS data,\n" +
        "           hstore_to_array(key || hstore(\n" +
        "               ARRAY['calendar_no', 'calendar_year'],\n" +
        "               ARRAY[al.calendar_no::text, al.calendar_year::text])) AS key\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_ACTIVE_LIST +" al\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON al.id::text = ent.key->'calendar_active_list_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime AND :endDateTime\n" +
        "           AND defined(ent.key, 'calendar_active_list_id')\n" +
        "   UNION\n" +
        "   SELECT sup.calendar_no::text AS calendar_no, sup.calendar_year::text AS calendar_year,\n" +
        "           action, action_date_time, sobi_fragment_id, table_name, hstore_to_array(data) AS data,\n" +
        "           hstore_to_array(key || hstore(" +
        "               ARRAY['calendar_no', 'calendar_year'],\n" +
        "               ARRAY[sup.calendar_no::text, sup.calendar_year::text])) AS key\n" +
        "       FROM ${schema}." + SqlTable.CALENDAR_SUPPLEMENTAL + " sup\n" +
        "           JOIN ${schema}." + SqlTable.SOBI_CHANGE_LOG + " ent\n" +
        "           ON sup.id::text = ent.key->'calendar_sup_id'\n" +
        "       WHERE action_date_time BETWEEN :startDateTime\n" +
        "           AND :endDateTime AND defined(ent.key, 'calendar_sup_id')\n" +
        ") AS digest\n" +
        "ON token.calendar_no = digest.calendar_no\n" +
        "   AND token.calendar_year = digest.calendar_year"
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
