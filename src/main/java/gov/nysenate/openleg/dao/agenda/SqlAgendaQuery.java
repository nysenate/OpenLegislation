package gov.nysenate.openleg.dao.agenda;

import gov.nysenate.openleg.dao.base.*;

public enum SqlAgendaQuery implements BasicSqlQuery
{
    /** --- Agenda Base --- */

    SELECT_AGENDAS_BY_YEAR(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA + " WHERE year = :year"
    ),
    SELECT_AGENDA_BY_ID(
        SELECT_AGENDAS_BY_YEAR.sql + " AND agenda_no = :agendaNo"
    ),
    UPDATE_AGENDA(
        "UPDATE ${schema}." + SqlTable.AGENDA + "\n" +
        "SET published_date_time = :publishedDateTime, modified_date_time = :modifiedDateTime, " +
        "    last_fragment_id = :lastFragmentId \n" +
        "WHERE agenda_no = :agendaNo AND year = :year"
    ),
    INSERT_AGENDA(
        "INSERT INTO ${schema}." + SqlTable.AGENDA + "\n" +
        "(agenda_no, year, published_date_time, modified_date_time, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :publishedDateTime, :modifiedDateTime, :lastFragmentId)"
    ),
    DELETE_AGENDA(
        "DELETE FROM ${schema}." + SqlTable.AGENDA + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year"
    ),

    /** --- Agenda Info Addendum --- */

    SELECT_AGENDA_INFO_ADDENDA(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_INFO_ADDENDUM + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year"
    ),
    SELECT_AGENDA_INFO_ADDENDUM(
        SELECT_AGENDA_INFO_ADDENDA.sql + " AND addendum_id = :addendumId"
    ),
    UPDATE_AGENDA_INFO_ADDENDUM(
        "UPDATE ${schema}." + SqlTable.AGENDA_INFO_ADDENDUM + "\n" +
        "SET modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, " +
        "    week_of = :weekOf, last_fragment_id = :lastFragmentId\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),
    INSERT_AGENDA_INFO_ADDENDUM(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_INFO_ADDENDUM + "\n" +
        "(agenda_no, year, addendum_id, modified_date_time, published_date_time, week_of, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :addendumId, :modifiedDateTime, :publishedDateTime, :weekOf, :lastFragmentId)"
    ),
    DELETE_AGENDA_INFO_ADDENDUM(
        "DELETE FROM ${schema}." + SqlTable.AGENDA_INFO_ADDENDUM + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),

    /** --- Agenda Info Committee --- */

    SELECT_AGENDA_INFO_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),
    SELECT_AGENDA_INFO_COMMITTEE_ID(
        "SELECT id FROM ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId\n" +
        "      AND committee_name = :committeeName AND committee_chamber = :committeeChamber::chamber"
    ),
    INSERT_AGENDA_INFO_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE + "\n" +
        "(agenda_no, year, addendum_id, committee_name, committee_chamber, chair, meeting_date_time, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :addendumId, :committeeName, :committeeChamber::chamber, :chair, :meetingDateTime, :lastFragmentId)"
    ),
    DELETE_AGENDA_INFO_COMMITTEE(
        "DELETE ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId " +
        "AND committee_name = :committeeName AND committee_chamber = :committeeChamber::chamber"
    ),

    /** --- Agenda Info Committee Item --- */

    SELECT_AGENDA_INFO_COMM_ITEMS(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE_ITEM + "\n" +
        "WHERE info_committee_id IN (" + SELECT_AGENDA_INFO_COMMITTEE_ID.sql + ")"
    ),
    INSERT_AGENDA_INFO_COMM_ITEM(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_INFO_COMMITTEE_ITEM + "\n" +
        "(info_committee_id, bill_print_no, bill_session_year, bill_amend_version, message, last_fragment_id)\n" +
        "SELECT c.id, :printNo, :session, :amendVersion, :message, :lastFragmentId\n" +
        "FROM (" + SELECT_AGENDA_INFO_COMMITTEE_ID.sql + ") c"
    );

    private String sql;

    SqlAgendaQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String envSchema) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema);
    }

    @Override
    public String getSql(String envSchema, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema, limitOffset);
    }

    @Override
    public String getSql(String envSchema, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema, orderBy, limitOffset);
    }
}