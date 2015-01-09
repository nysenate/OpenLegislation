package gov.nysenate.openleg.dao.agenda.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

import java.util.Arrays;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.base.SqlTable.*;

public enum SqlAgendaUpdatesQuery implements BasicSqlQuery
{
    SELECT_AGENDA_UPDATES_FRAGMENT(
        "SELECT key->'agenda_no' AS agenda_no, key->'year' AS year,\n" +
        "       %s \n" + // Any additional columns are replaced here
        "FROM ${schema}." + SqlTable.SOBI_CHANGE_LOG + " log\n" +
        "LEFT JOIN ${schema}." + SqlTable.SOBI_FRAGMENT + " sobi\n" +
        "     ON log.sobi_fragment_id = sobi.fragment_id\n" +
        "WHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime\n" +
        "AND table_name IN (" + // Using the table name here because it's slightly faster than a key search
            Arrays.asList(AGENDA, AGENDA_INFO_ADDENDUM, AGENDA_INFO_COMMITTEE, AGENDA_VOTE_ADDENDUM, AGENDA_VOTE_COMMITTEE)
                    .stream().map(table -> "'" + table + "'").collect(Collectors.joining(",")) +
        ")\n" +
        "%s\n" + // Additional WHERE clause
        "%s" // GROUP BY clause if necessary
    ),

    SELECT_COLUMNS_FOR_DIGEST_FRAGMENT(
        "sobi.fragment_id AS last_fragment_id, log.action_date_time AS last_processed_date_time, \n" +
        "sobi.published_date_time AS last_published_date_time, COUNT(*) OVER () AS total_updated,\n" +
        "table_name, action, hstore_to_array(key) AS key, hstore_to_array(data) AS data\n"
    ),

    SELECT_AGENDA_UPDATE_TOKENS(
        String.format(SELECT_AGENDA_UPDATES_FRAGMENT.sql,
            // Select columns
            "       MAX(action_date_time) AS last_processed_date_time, MAX(sobi.fragment_id) AS last_fragment_id,\n" +
            "       MAX(sobi.published_date_time) AS last_published_date_time, COUNT(*) OVER() AS total_updated\n",
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
            "AND key @> hstore(ARRAY['agenda_no', 'year'], ARRAY[:agendaNo, :year::text])\n",
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
