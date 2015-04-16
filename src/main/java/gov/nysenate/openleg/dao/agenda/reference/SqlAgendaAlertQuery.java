package gov.nysenate.openleg.dao.agenda.reference;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlAgendaAlertQuery implements BasicSqlQuery{

    SELECT_INFO_COMMITTEE(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE +" a\n" +
        "   LEFT JOIN ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE_ITEM + " ai\n" +
        "   ON a.id = ai.alert_info_committee_id"
    ),
    SELECT_INFO_COMMITTEE_BY_ID(
        SELECT_INFO_COMMITTEE.sql + "\n" +
        "WHERE a.reference_date_time = :referenceDateTime AND a.week_of = :weekOf AND a.addendum_id = :addendumId" +
        "   AND a.chamber = :chamber::chamber AND a.committee_name = :committeeName"
    ),
    SELECT_UNCHECKED_IN_RANGE(
        SELECT_INFO_COMMITTEE.sql + "\n" +
        "WHERE checked = FALSE AND reference_date_time BETWEEN :startDateTime AND :endDateTime"
    ),
    INSERT_INFO_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE + "\n" +
        "       ( reference_date_time, week_of, addendum_id, chamber,           committee_name, " +
        "       chair,  location,  meeting_date_time, notes)" +
        "VALUES (:referenceDateTime,  :weekOf, :addendumId, :chamber::chamber, :committeeName, " +
        "      :chair, :location, :meetingDateTime,  :notes)"
    ),
    INSERT_INFO_COMMITTEE_ITEM(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE_ITEM + "\n" +
        "       ( alert_info_committee_id, bill_print_no, bill_session_year, bill_amend_version, message)" +
        "VALUES (:alertInfoCommitteeId,   :billPrintNo,  :billSessionYear,  :billAmendVersion,  :message)"
    ),
    SET_INFO_COMMITTEE_CHECKED(
        "UPDATE ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE + "\n" +
        "SET checked = :checked\n" +
        "WHERE reference_date_time = :referenceDateTime AND week_of = :weekOf AND chamber = :chamber::chamber\n" +
        "       AND committee_name = :committeeName AND addendum_id = :addendumId"
    ),
    DELETE_INFO_COMMITTEE(
        "DELETE FROM ${schema}." + SqlTable.AGENDA_ALERT_INFO_COMMITTEE + "\n" +
        "WHERE reference_date_time = :referenceDateTime AND week_of = :weekOf AND addendum_id = :addendumId" +
        "   AND chamber = :chamber::chamber AND committee_name = :committeeName"
    ),
    ;

    private String sql;

    private SqlAgendaAlertQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
