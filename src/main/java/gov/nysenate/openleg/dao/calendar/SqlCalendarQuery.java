package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.SqlQueryEnum;
import gov.nysenate.openleg.dao.base.SqlTable;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum SqlCalendarQuery implements SqlQueryEnum
{
    /** --- Calendar Base --- */

    SELECT_CALENDAR_SQL(
        "SELECT * FROM ${schema}." + SqlTable.CALENDAR + "\n" +
        "WHERE calendar_no = :calendarNo AND year = :year"
    ),
    SELECT_CALENDARS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.CALENDAR + "\n" +
        "WHERE year = :year"
    ),
    DELETE_CALENDAR_SQL(
        "DELETE FROM ${schema}." + SqlTable.CALENDAR + "\n" +
        "WHERE calendar_no = :calendarNo AND year = :year"
    ),
    UPDATE_CALENDAR_SQL(
        "UPDATE ${schema}." + SqlTable.CALENDAR + "\n" +
        "SET modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, " +
        "    last_fragment_id = :lastFragmentId\n" +
        "WHERE calendar_no = :calendarNo AND year = :year"
    ),
    INSERT_CALENDAR_SQL(
        "INSERT INTO ${schema}." + SqlTable.CALENDAR + "\n" +
        "(calendar_no, year, modified_date_time, published_date_time, last_fragment_id) \n" +
        "VALUES (:calendarNo, :year, :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    );

    private String sql;

    SqlCalendarQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String environmentSchema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", environmentSchema);
        return new StrSubstitutor(replaceMap).replace(this.sql);
    }
}
