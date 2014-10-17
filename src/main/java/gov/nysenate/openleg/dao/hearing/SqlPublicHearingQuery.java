package gov.nysenate.openleg.dao.hearing;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlPublicHearingQuery implements BasicSqlQuery
{
    SELECT_PUBLIC_HEARING_IDS_BY_YEAR(
        "SELECT title, date_time FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "WHERE EXTRACT(YEAR FROM date_time) = :year"
    ),
    SELECT_PUBLIC_HEARING_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "WHERE title = :title AND date_time = :dateTime"
    ),
    UPDATE_PUBLIC_HEARING(
        "UPDATE ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "SET public_hearing_file = :publicHearingFile, " +
        "address = :address, text = :text" + "\n" +
        "WHERE title = :title AND date_time = :dateTime"
    ),
    INSERT_PUBLIC_HEARING(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "(title, date_time, public_hearing_file, address, text)" + "\n" +
        "VALUES (:title, :dateTime, :publicHearingFile, :address, :text)"
    ),
    SELECT_PUBLIC_HEARING_ATTENDANCE(
        "SELECT session_member_id, date_time FROM ${schema}." + SqlTable.PUBLIC_HEARING_ATTENDANCE + "\n" +
        "WHERE title = :title AND date_time = :dateTime"
    ),
    DELETE_PUBLIC_HEARING_ATTENDANCE(
        "DELETE FROM ${schema}." + SqlTable.PUBLIC_HEARING_ATTENDANCE + "\n" +
        "WHERE title = :title AND date_time = :dateTime AND session_member_id = :sessionMemberId"
    ),
    INSERT_PUBLIC_HEARING_ATTENDANCE(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING_ATTENDANCE + "\n" +
        "(title, date_time, session_member_id) " +
        "VALUES (:title, :dateTime, :sessionMemberId)"
    ),
    SELECT_PUBLIC_HEARING_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "WHERE title = :title AND date_time = :dateTime"
    ),
    DELETE_PUBLIC_HEARING_COMMITTEE(
        "DELETE FROM ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "WHERE title = :title AND date_time = :dateTime AND " +
        "committee_name = :committeeName AND committee_chamber = :committeeChamber"
    ),
    INSERT_PUBLIC_HEARING_COMMITTEES(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "(title, date_time, committee_name, committee_chamber) " +
        "VALUES (:title, :dateTime, :committeeName, :committeeChamber)"
    );

    private String sql;

    SqlPublicHearingQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
