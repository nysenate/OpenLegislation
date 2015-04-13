package gov.nysenate.openleg.dao.agenda.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlAgendaQuery implements BasicSqlQuery
{
    /** --- Agenda Base --- */

    SELECT_AGENDAS_BY_YEAR(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA + " WHERE year = :year"
    ),
    SELECT_AGENDA_BY_ID(
        SELECT_AGENDAS_BY_YEAR.sql + " AND agenda_no = :agendaNo"
    ),
    SELECT_AGENDA_BY_WEEK_OF(
        "SELECT a.agenda_no, a.year, a.modified_date_time, a.published_date_time\n" +
        "FROM ${schema}." + SqlTable.AGENDA + " a\n" +
        "   JOIN ${schema}." + SqlTable.AGENDA_INFO_ADDENDUM + " ai\n" +
        "   ON a.agenda_no = ai.agenda_no AND a.year = ai.year\n" +
        "WHERE ai.week_of = :weekOf"
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
        "(agenda_no, year, addendum_id, committee_name, committee_chamber, chair, location, meeting_date_time, " +
        " notes, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :addendumId, :committeeName, :committeeChamber::chamber, :chair, :location, " +
        "        :meetingDateTime, :notes, :lastFragmentId)"
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
    ),

    /** --- Agenda Vote Addendum --- */

    SELECT_AGENDA_VOTE_ADDENDA(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_VOTE_ADDENDUM + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year"
    ),
    SELECT_AGENDA_VOTE_ADDENDUM(
        SELECT_AGENDA_VOTE_ADDENDA.sql + " AND addendum_id = :addendumId"
    ),
    UPDATE_AGENDA_VOTE_ADDENDUM(
        "UPDATE ${schema}." + SqlTable.AGENDA_VOTE_ADDENDUM + "\n" +
        "SET modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, " +
        "    last_fragment_id = :lastFragmentId\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),
    INSERT_AGENDA_VOTE_ADDENDUM(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_VOTE_ADDENDUM + "\n" +
        "(agenda_no, year, addendum_id, modified_date_time, published_date_time, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :addendumId, :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),
    DELETE_AGENDA_VOTE_ADDENDUM(
        "DELETE FROM ${schema}." + SqlTable.AGENDA_VOTE_ADDENDUM + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),

    /** --- Agenda Vote Committee --- */

    SELECT_AGENDA_VOTE_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId"
    ),
    SELECT_AGENDA_VOTE_COMMITTEE_ID(
        "SELECT id FROM ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId\n" +
        "      AND committee_name = :committeeName AND committee_chamber = :committeeChamber::chamber"
    ),
    INSERT_AGENDA_VOTE_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE + "\n" +
        "(agenda_no, year, addendum_id, committee_name, committee_chamber, chair, meeting_date_time, last_fragment_id)\n" +
        "VALUES (:agendaNo, :year, :addendumId, :committeeName, :committeeChamber::chamber, :chair, " +
        "        :meetingDateTime, :lastFragmentId)"
    ),
    DELETE_AGENDA_VOTE_COMMITTEE(
        "DELETE ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE + "\n" +
        "WHERE agenda_no = :agendaNo AND year = :year AND addendum_id = :addendumId AND " +
        "      committee_name = :committeeName AND committee_chamber = :committeeChamber::chamber"
    ),

    /** --- Agenda Vote Attendance --- */

    SELECT_AGENDA_VOTE_ATTENDANCE(
        "SELECT * FROM ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE_ATTEND + "\n" +
        "WHERE vote_committee_id IN (" + SELECT_AGENDA_VOTE_COMMITTEE_ID.sql + ")"
    ),
    INSERT_AGENDA_VOTE_ATTENDANCE(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE_ATTEND + "\n" +
        "(vote_committee_id, session_member_id, session_year, lbdc_short_name, rank, party, attend_status, last_fragment_id)\n" +
        "SELECT c.id, :sessionMemberId, :sessionYear, :lbdcShortName, :rank, :party, :attendStatus, :lastFragmentId\n" +
        "FROM (" + SELECT_AGENDA_VOTE_COMMITTEE_ID.sql + ") c"
    ),

    /** --- Agenda Committee Votes --- */

    SELECT_AGENDA_COMM_VOTES(
        "SELECT cv.id, cv.vote_action, cv.refer_committee_name, cv.refer_committee_chamber, cv.with_amendment," +
        "       vi.bill_print_no, vi.bill_session_year, vi.bill_amend_version, vi.vote_date, vi.vote_type," +
        "       vi.sequence_no, vi.published_date_time, vi.modified_date_time," +
        "       vi.committee_name, vi.committee_chamber," +
        "       vr.session_member_id, vr.session_year, vr.vote_code\n" +
        "FROM ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE_VOTE + " cv\n" +
        "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " vi ON cv.vote_info_id = vi.id\n" +
        "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + " vr ON vi.id = vr.vote_id\n" +
        "WHERE cv.vote_committee_id IN (" + SELECT_AGENDA_VOTE_COMMITTEE_ID.sql + ")"
    ),
    INSERT_AGENDA_COMM_BILL_VOTES(
        "INSERT INTO ${schema}." + SqlTable.AGENDA_VOTE_COMMITTEE_VOTE + "\n" +
        "(vote_committee_id, vote_action, vote_info_id, refer_committee_name, refer_committee_chamber, with_amendment," +
        " last_fragment_id) \n" +
        "SELECT c.id, :voteAction, vi.id, :referCommitteeName, :referCommitteeChamber::chamber, :withAmend, " +
        "       :lastFragmentId\n" +
        "FROM (" + SELECT_AGENDA_VOTE_COMMITTEE_ID.sql + ") c, " +
        "     ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " vi\n" +
        "WHERE vi.bill_print_no = :billPrintNo AND vi.bill_session_year = :sessionYear AND \n" +
        "      vi.bill_amend_version = :amendVersion AND vote_date = :voteDate AND sequence_no = :sequenceNo AND" +
        "      vi.vote_type = :voteType::${schema}.vote_type"
    );

    private String sql;

    SqlAgendaQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}