package gov.nysenate.openleg.dao.entity.committee.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCommitteeQuery implements BasicSqlQuery
{
    SELECT_COMMITTEE_ID(
            "SELECT chamber, name FROM ${schema}." + SqlTable.COMMITTEE
    ),
    SELECT_SESSION_YEARS(
            "SELECT DISTINCT session_year FROM ${schema}." + SqlTable.COMMITTEE_VERSION
    ),
    SELECT_COMMITTEE_SESSION_IDS(
            "SELECT DISTINCT chamber, committee_name, session_year FROM ${schema}." + SqlTable.COMMITTEE_VERSION
    ),
    SELECT_COMMITTEE_VERSION_BASE(
            "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + " cv" + "\n" +
            "   JOIN ${schema}." + SqlTable.COMMITTEE_MEMBER + " cm" + "\n" +
            "ON cv.chamber = cm.chamber AND cv.committee_name = cm.committee_name AND cv.created = cm.version_created"
    ),
    SELECT_COMMITTEE_VERSION_HISTORY(
            SELECT_COMMITTEE_VERSION_BASE.sql + "\n" +
            "WHERE cv.committee_name = :committeeName::citext AND cv.chamber = :chamber::chamber" + "\n" +
            "   AND cv.session_year = :sessionYear"
    ),
    SELECT_COMMITTEE_CURRENT_SQL(
            SELECT_COMMITTEE_VERSION_HISTORY.sql + "\n" +
            "WHERE cv.reformed = 'infinity'::timestamp without time zone"
    ),
    SELECT_COMMITTEE_AT_DATE_SQL(
            SELECT_COMMITTEE_VERSION_HISTORY.sql + "\n" +
            "   AND :referenceDate >= cv.created" + "\n" +
            "   AND :referenceDate < cv.reformed"
    ),
    SELECT_COMMITTEE_CURRENT_VERSION(
            "SELECT current_version FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
            "WHERE name=:committeeName::citext AND chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_PREVIOUS_COMMITTEE_VERSION(
            "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber) " + "\n" +
            "   AND session_year=:sessionYear AND :referenceDate > created" + "\n" +
            "ORDER BY created DESC" + "\n" +
            "LIMIT 1"
    ),
    INSERT_COMMITTEE(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE + " (name, chamber)" + "\n" +
            "VALUES (:committeeName, CAST(:chamber AS chamber))"
    ),
    INSERT_COMMITTEE_VERSION(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "        (committee_name, chamber, session_year, location, meetday, meettime, meetaltweek,  " + "\n" +
            "           meetaltweektext, created, last_fragment_id)" + "\n" +
            "VALUES (:committeeName, :chamber::chamber, :sessionYear, :location, :meetday, :meettime, :meetaltweek, " + "\n" +
            "           :meetaltweektext, :referenceDate, :lastFragmentId)"
    ),
    INSERT_COMMITTEE_MEMBER(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE_MEMBER +
            " (committee_name, chamber, version_created, session_member_id, session_year, sequence_no, title, majority)" + "\n" +
            "VALUES (:committeeName, CAST(:chamber AS chamber), :referenceDate, :session_member_id, :sessionYear, :sequence_no, CAST(:title AS committee_member_title), :majority)"
    ),
    UPDATE_COMMITTEE_MEETING_INFO(
            "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "SET location=:location, meetday=:meetday, meettime=:meettime, meetaltweek=:meetaltweek, " + "\n" +
            "       meetaltweektext=:meetaltweektext, last_fragment_id=:lastFragmentId" + "\n" +
            "WHERE committee_name=:committeeName::citext  AND chamber=CAST(:chamber AS chamber) " + "\n" +
            "       AND session_year=:sessionYear AND created=:referenceDate"
    ),
    UPDATE_COMMITTEE_CURRENT_VERSION(
            "UPDATE ${schema}." + SqlTable.COMMITTEE + "\n" +
            "SET current_version=:referenceDate, current_session=:sessionYear" + "\n" +
            "WHERE name=:committeeName::citext AND chamber=CAST(:chamber AS chamber) AND current_session<=:sessionYear"
    ),
    UPDATE_COMMITTEE_VERSION_REFORMED(
            "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "SET reformed=:reformed, last_fragment_id=:lastFragmentId" + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber) " + "\n" +
            "   AND session_year=:sessionYear AND created=:referenceDate"
    ),
    UPDATE_COMMITTEE_VERSION_REFORMED_CURRENT(
            "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "SET reformed='infinity'::timestamp without time zone, last_fragment_id=:lastFragmentId" + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber) " + "\n" +
            "   AND session_year=:sessionYear AND created=:referenceDate"
    ),
    DELETE_COMMITTEE(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
            "WHERE name=:committeeName::citext AND chamber=CAST(:chamber AS chamber)"
    ),
    DELETE_COMMITTEE_VERSION_FUTURE(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber)" + "\n" +
            "   AND session_year=:sessionYear AND created>:referenceDate"
    ),
    DELETE_COMMITTEE_VERSION(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber)" + "\n" +
            "   AND session_year=:sessionYear AND created=:referenceDate"
    ),
    DELETE_COMMITTEE_MEMBERS(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
            "WHERE committee_name=:committeeName::citext AND chamber=CAST(:chamber AS chamber)" + "\n" +
            "   AND session_year=:sessionYear AND version_created=:referenceDate"
    );

    private String sql;

    SqlCommitteeQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}