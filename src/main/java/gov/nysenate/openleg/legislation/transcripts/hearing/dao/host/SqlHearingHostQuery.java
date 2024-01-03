package gov.nysenate.openleg.legislation.transcripts.hearing.dao.host;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.HEARING_HOST;
import static gov.nysenate.openleg.common.dao.SqlTable.HEARING_HOST_HEARING_ID_PAIRS;

public enum SqlHearingHostQuery implements BasicSqlQuery {
    SELECT_HOST_IDS_BY_HEARING_ID (
            "SELECT hearing_host_id FROM ${schema}." + HEARING_HOST_HEARING_ID_PAIRS + "\n" +
                "WHERE hearing_id = :hearing_id"
    ),
    SELECT_HOSTS_BY_HEARING_ID (
            "SELECT * FROM ${schema}." + HEARING_HOST + "\n" +
                "INNER JOIN (" + SELECT_HOST_IDS_BY_HEARING_ID.sql + ") as x\n" +
                "ON " + HEARING_HOST + ".id = x.hearing_host_id" + "\n"
    ),
    SELECT_HOST_ID (
            "SELECT id FROM ${schema}." + HEARING_HOST + "\n" +
                "WHERE name = :name AND chamber = :chamber::chamber AND type = :type::hearing_host_type"
    ),
    INSERT_HOST (
            "INSERT INTO ${schema}." + HEARING_HOST + "\n" +
                "(name, chamber, type)" + "\n" +
                "VALUES (:name, :chamber::chamber, :type::hearing_host_type)"
    ),
    DELETE_HOST_BY_ID (
            "DELETE FROM ${schema}." + HEARING_HOST + "\n" +
                "WHERE id = :id"
    ),
    SELECT_HEARING_ID_BY_HOST_ID (
            "SELECT hearing_id FROM ${schema}." + HEARING_HOST_HEARING_ID_PAIRS + "\n" +
                "WHERE hearing_host_id = :hearing_host_id"
    ),
    INSERT_HOST_HEARING_ID_PAIR (
            "INSERT INTO ${schema}." + HEARING_HOST_HEARING_ID_PAIRS + "\n" +
                "(hearing_host_id, hearing_id)" + "\n" +
                "VALUES (:hearing_host_id, :hearing_id)"
    ),
    DELETE_HOSTS_WITH_HEARING_ID (
            "DELETE FROM ${schema}." + HEARING_HOST_HEARING_ID_PAIRS + "\n" +
                "WHERE hearing_id = :hearing_id"
    );

    private final String sql;

    SqlHearingHostQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
