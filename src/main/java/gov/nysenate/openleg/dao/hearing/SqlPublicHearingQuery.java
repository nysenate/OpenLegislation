package gov.nysenate.openleg.dao.hearing;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlPublicHearingQuery implements BasicSqlQuery
{
    SELECT_PUBLIC_HEARING_IDS(
        "SELECT filename FROM ${schema}." + SqlTable.PUBLIC_HEARING
    ),
    SELECT_PUBLIC_HEARING_BY_ID(
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "WHERE filename = :filename"
    ),
    UPDATE_PUBLIC_HEARING(
        "UPDATE ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "SET title = :title, date = :date, address = :address, text = :text, " +
        "start_time = :startTime, end_time = :endTime, " +
        "modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime" + "\n" +
        "WHERE filename = :filename"
    ),
    INSERT_PUBLIC_HEARING(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
        "(filename, date, title, address, text, start_time, end_time)" + "\n" +
        "VALUES (:filename, :date, :title, :address, :text, :startTime, :endTime)"
    ),
    SELECT_PUBLIC_HEARING_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "WHERE filename = :filename"
    ),
    DELETE_PUBLIC_HEARING_COMMITTEE(
        "DELETE FROM ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "WHERE filename = :filename AND committee_name = :committeeName " +
        "AND committee_chamber = :committeeChamber::chamber)"
    ),
    INSERT_PUBLIC_HEARING_COMMITTEES(
        "INSERT INTO ${schema}." + SqlTable.PUBLIC_HEARING_COMMITTEE + "\n" +
        "(filename, committee_name, committee_chamber) " +
        "VALUES (:filename, :committeeName, :committeeChamber::chamber)"
    ),
    SELECT_PUBLIC_HEARING_UPDATES(
            "SELECT filename, modified_date_time, COUNT(*) OVER() as total_updated " +
            "FROM ${schema}." + SqlTable.PUBLIC_HEARING + "\n" +
            "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime"
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
