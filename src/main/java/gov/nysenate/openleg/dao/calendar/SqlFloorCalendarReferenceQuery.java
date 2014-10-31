package gov.nysenate.openleg.dao.calendar;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 10/24/14.
 */
public enum SqlFloorCalendarReferenceQuery implements BasicSqlQuery {

    /*    floor Calendar      */

    SELECT_FLOOR_CALENDAR(
            "SELECT * FROM ${schema}." + SqlTable.FLOOR_CALENDAR_REFERENCE + "\n" +
                    "WHERE calendar_no = :calendarNo AND calendar_year = :calendar_year" +
                    " AND version = :version AND referenced_date = :referenced_date"
    ),
    SELECT_FLOOR_CALENDARS_BY_YEAR(
            "SELECT * FROM ${schema}." + SqlTable.FLOOR_CALENDAR_REFERENCE + "\n" +
                    "WHERE year = :year"
    ),
    SELECT_CALENDAR_SUPS_BY_YEAR(
            "SELECT * FROM ${schema}." + SqlTable.CALENDAR_SUPPLEMENTAL + " sup" + "\n" +
                    "   JOIN ${schema}." + SqlTable.CALENDAR_SUP_ENTRY + " ent" + "\n" +
                    "       ON sup.id = ent.calendar_sup_id" + "\n" +
                    "WHERE calendar_year = :year"
    ),






    ;



    private String sql;
    SqlFloorCalendarReferenceQuery(String sql) {
        this.sql = sql;
    }
    @Override
    public String getSql() {
        return this.sql;
    }


}
