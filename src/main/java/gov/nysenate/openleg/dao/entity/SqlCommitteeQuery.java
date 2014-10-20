package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCommitteeQuery implements BasicSqlQuery
{
    SELECT_COMMITTEE_CURRENT_SQL(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE + " JOIN ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "ON " + SqlTable.COMMITTEE + ".name=" + SqlTable.COMMITTEE_VERSION + ".committee_name" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".chamber=" + SqlTable.COMMITTEE_VERSION + ".chamber" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_version=" + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_session=" + SqlTable.COMMITTEE_VERSION + ".session_year" + "\n" +
        "WHERE " + SqlTable.COMMITTEE + ".name=:committeeName" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_COMMITTEE_AT_DATE_SQL(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear" + "\n" +
            "AND :referenceDate >= " + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND :referenceDate < " + SqlTable.COMMITTEE_VERSION + ".reformed"
    ),
    SELECT_COMMITTEE_MEMBERS(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber)" + "\n" +
            "AND session_year=:sessionYear AND version_created=:referenceDate"
    ),
    SELECT_COMMITTEE_CURRENT_VERSION(
        "SELECT current_version FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
        "WHERE name=:committeeName AND chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_COMMITTEES_BY_CHAMBER(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE + " JOIN ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "ON " + SqlTable.COMMITTEE + ".name=" + SqlTable.COMMITTEE_VERSION + ".committee_name" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".chamber=" + SqlTable.COMMITTEE_VERSION + ".chamber" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_version=" + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_session=" + SqlTable.COMMITTEE_VERSION + ".session_year" + "\n" +
        "WHERE " + SqlTable.COMMITTEE + ".chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_COMMITTEES_BY_CHAMBER_COUNT(
        SELECT_COMMITTEES_BY_CHAMBER.sql.replaceFirst("SELECT \\*", "SELECT COUNT(*)")
    ),
    SELECT_COMMITTEE_VERSION_HISTORY(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber)" + "\n" +
        "   AND session_year BETWEEN :sessionYearBegin AND :sessionYearEnd" + "\n" +
        "   AND (created BETWEEN :dateRangeBegin AND :dateRangeEnd" + "\n" +
        "       OR reformed BETWEEN :dateRangeBegin AND :dateRangeEnd)"
    ),
    SELECT_COMMITTEE_VERSION_HISTORY_COUNT(
        SELECT_COMMITTEE_VERSION_HISTORY.sql.replaceFirst("SELECT \\*", "SELECT COUNT(*)")
    ),
    SELECT_PREVIOUS_COMMITTEE_VERSION(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND :referenceDate > created" + "\n" +
        "ORDER BY created DESC" + "\n" +
        "LIMIT 1"
    ),
    SELECT_NEXT_COMMITTEE_VERSION(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND :referenceDate < created" + "\n" +
        "ORDER BY created" + "\n" +
        "LIMIT 1"
    ),
    INSERT_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE + " (name, chamber)" + "\n" +
        "VALUES (:committeeName, CAST(:chamber AS chamber))"
    ),
    INSERT_DEFAULT_COMMITTEE_VERSION(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "       ( committee_name, chamber,  session_year, created)" + "\n" +
        "VALUES (:committeeName, CAST(:chamber AS chamber), :sessionYear, CAST('-infinity' as timestamp without time zone))"
    ),
    INSERT_COMMITTEE_VERSION(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE_VERSION +
        " (committee_name, chamber, session_year, location, meetday, meettime, meetaltweek, meetaltweektext, created)" + "\n" +
        "VALUES (:committeeName, CAST(:chamber AS chamber), :sessionYear, :location, :meetday, :meettime, :meetaltweek, :meetaltweektext, :referenceDate)"
    ),
    INSERT_COMMITTEE_MEMBER(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE_MEMBER +
        " (committee_name, chamber, version_created, session_member_id, session_year, sequence_no, title, majority)" + "\n" +
        "VALUES (:committeeName, CAST(:chamber AS chamber), :referenceDate, :session_member_id, :sessionYear, :sequence_no, CAST(:title AS committee_member_title), :majority)"
    ),
    UPDATE_COMMITTEE_MEETING_INFO(
        "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "SET location=:location, meetday=:meetday, meettime=:meettime, meetaltweek=:meetaltweek, meetaltweektext=:meetaltweektext" + "\n" +
        "WHERE committee_name=:committeeName  AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND created=:referenceDate"
    ),
    UPDATE_COMMITTEE_CURRENT_VERSION(
        "UPDATE ${schema}." + SqlTable.COMMITTEE + "\n" +
        "SET current_version=:referenceDate, current_session=:sessionYear" + "\n" +
        "WHERE name=:committeeName AND chamber=CAST(:chamber AS chamber) AND current_session<=:sessionYear"
    ),
    UPDATE_COMMITTEE_VERSION_REFORMED(
        "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "SET reformed=:reformed" + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND created=:referenceDate"
    ),
    DELETE_COMMITTEE(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
        "WHERE name=:committeeName AND chamber=CAST(:chamber AS chamber)"
    ),
    DELETE_COMMITTEE_VERSION(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND created=:referenceDate"
    ),
    DELETE_COMMITTEE_MEMBERS(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
        "WHERE committee_name=:committeeName AND chamber=CAST(:chamber AS chamber) AND session_year=:sessionYear AND version_created=:referenceDate"
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