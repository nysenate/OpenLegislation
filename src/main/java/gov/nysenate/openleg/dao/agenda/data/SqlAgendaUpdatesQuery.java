package gov.nysenate.openleg.dao.agenda.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

import java.util.Arrays;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.base.SqlTable.*;

public enum SqlAgendaUpdatesQuery implements BasicSqlQuery
{
    SELECT_AGENDA_UPDATES_FRAGMENT(
        "SELECT agenda_no, year, %s\n" + // Any additional columns are replaced here
        "FROM ${schema}." + SqlTable.AGENDA_CHANGE_LOG + "\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "%s\n" + // Additional WHERE clause
        "%s" // GROUP BY clause if necessary
    ),

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "sobi_fragment_id AS last_fragment_id, action_date_time AS last_processed_date_time, \n" +
        "published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action, hstore_to_array(data) AS data\n"
    ),

    SELECT_AGENDA_UPDATE_TOKENS(
        String.format(SELECT_AGENDA_UPDATES_FRAGMENT.sql,
            // Select columns
            "       MAX(action_date_time) AS last_processed_date_time, MAX(sobi_fragment_id) AS last_fragment_id,\n" +
            "       MAX(published_date_time) AS last_published_date_time, COUNT(*) OVER() AS total_updated\n",
            // No extra where clause
            "",
            // Group by agenda id
            "GROUP BY agenda_no, year")
    ),

    SELECT_AGENDA_UPDATE_DIGESTS(
        String.format(SELECT_AGENDA_UPDATES_FRAGMENT.sql,
            // Select columns
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            // No extra where clause
            "",
            // No group by needed for digests due to pagination/performance issues
            "")
    ),

    SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_AGENDA(
        String.format(SELECT_AGENDA_UPDATES_FRAGMENT.sql,
            // Select columns
            SELECT_COLUMNS_FOR_DIGEST_FRAGMENT.sql,
            // No extra where clause
            "AND agenda_no = :agendaNo AND year = :year",
            // No group by needed for digests due to pagination/performance issues
            "")
    );

    private String sql;

    SqlAgendaUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
