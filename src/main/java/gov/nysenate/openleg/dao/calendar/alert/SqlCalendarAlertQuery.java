package gov.nysenate.openleg.dao.calendar.alert;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCalendarAlertQuery implements BasicSqlQuery {
    /** --- Calendar Base --- */

    SELECT_CALENDAR(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year"
    ),
    SELECT_CALENDAR_IDS(
        "SELECT calendar_no, calendar_year FROM ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "WHERE calendar_year = :year"
    ),
    MARK_CHECKED(
        "UPDATE ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "Set checked = :checked" + "\n" +
        "WHERE calendar_no = :calendarNo and calendar_year = :year"
    ),
    MARK_PROD_CHECKED(
        "UPDATE ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "Set prod_checked = :prodChecked" + "\n" +
        "WHERE calendar_no = :calendarNo and calendar_year = :year"
    ),
    SELECT_UNCHECKED(
            "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
            "WHERE checked = :checked"
    ),
    SELECT_PROD_UNCHECKED(
            "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
            "WHERE prod_checked = :prodChecked"
    ),
    UPDATE_CALENDAR(
        "UPDATE ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "SET modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, " +
        "    last_file = :lastFile\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year"
    ),
    INSERT_CALENDAR(
        "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "(calendar_no, calendar_year, modified_date_time, published_date_time, last_file) \n" +
        "VALUES (:calendarNo, :year, :modifiedDateTime, :publishedDateTime, :lastFile)"
    ),
    SELECT_CALENDAR_RANGE(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR + "\n" +
        "WHERE published_date_time BETWEEN :startTime AND :endTime"
    ),

    /** --- Calendar Supplemental --- */

    SELECT_CALENDAR_SUPS_BY_YEAR(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUPPLEMENTAL + " sup" + "\n" +
        "   JOIN ${schema}." + SqlTable.ALERT_CALENDAR_SUP_ENTRY + " ent" + "\n" +
        "       ON sup.id = ent.calendar_sup_id" + "\n" +
        "WHERE calendar_year = :year"
    ),
    SELECT_CALENDAR_SUPS(
        SELECT_CALENDAR_SUPS_BY_YEAR.sql + " AND calendar_no = :calendarNo"
    ),
    SELECT_CALENDAR_SUP(
        SELECT_CALENDAR_SUPS.sql + " AND sup_version = :supVersion"
    ),
    SELECT_CALENDAR_SUP_ID(
        "SELECT id FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUPPLEMENTAL + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sup_version = :supVersion"
    ),
    INSERT_CALENDAR_SUP(
        "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR_SUPPLEMENTAL + "\n" +
        "(calendar_no, calendar_year, sup_version, calendar_date, release_date_time, " +
        " modified_date_time, published_date_time, last_file) \n" +
        "VALUES (:calendarNo, :year, :supVersion, :calendarDate, :releaseDateTime, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFile)"
    ),
    DELETE_CALENDAR_SUP(
        "DELETE FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUPPLEMENTAL + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sup_version = :supVersion"
    ),

    /** --- Calendar Supplemental Entries --- */

    SELECT_CALENDAR_SUP_ENTRIES(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUP_ENTRY + "\n" +
        "WHERE calendar_sup_id IN (" + SELECT_CALENDAR_SUP_ID.sql + ")"
    ),
    SELECT_CALENDAR_SUP_ENTRIES_BY_SECTION(
        SELECT_CALENDAR_SUP_ENTRIES.sql + " AND section_code = :sectionCode"
    ),
    INSERT_CALENDAR_SUP_ENTRY(
        "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR_SUP_ENTRY + "\n" +
        "(calendar_sup_id, section_code, bill_calendar_no, bill_print_no, bill_amend_version, bill_session_year, \n" +
        " sub_bill_print_no, sub_bill_amend_version, sub_bill_session_year, high, last_file)\n" +
        "SELECT id, :sectionCode, :billCalNo, :printNo, :amendVersion, :session, :subPrintNo, :subAmendVersion, " +
        "       :subSession, :high, :lastFile\n" +
        "FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUPPLEMENTAL + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sup_version = :supVersion"
    ),
    DELETE_CALENDAR_SUP_ENTRIES(
        "DELETE FROM ${schema}." + SqlTable.ALERT_CALENDAR_SUP_ENTRY + "\n" +
        "WHERE calendar_sup_id IN (" + SELECT_CALENDAR_SUP_ID.sql + ")"
    ),

    /** --- Calendar Active List --- */

    SELECT_CALENDAR_ACTIVE_LISTS_BY_YEAR(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + " al" + "\n" +
        "   JOIN ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST_ENTRY + " ent" + "\n" +
        "       ON al.id = ent.calendar_active_list_id" + "\n" +
        "WHERE calendar_year = :year"
    ),
    SELECT_CALENDAR_ACTIVE_LISTS_BY_YEAR_COUNT(
        "SELECT COUNT(*) FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + " al" + "\n" +
        "WHERE calendar_year = :year"
    ),
    SELECT_CALENDAR_ACTIVE_LIST_IDS(
        "SELECT calendar_no, calendar_year, sequence_no FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + "\n" +
        "WHERE calendar_year = :year"
    ),
    SELECT_CALENDAR_ACTIVE_LIST_ID_COUNT(
        SELECT_CALENDAR_ACTIVE_LIST_IDS.sql.replace("calendar_no, calendar_year, sequence_no", "COUNT(*)")
    ),
    SELECT_CALENDAR_ACTIVE_LISTS(
        SELECT_CALENDAR_ACTIVE_LISTS_BY_YEAR.sql + " AND calendar_no = :calendarNo"
    ),
    SELECT_CALENDAR_ACTIVE_LIST(
        SELECT_CALENDAR_ACTIVE_LISTS.sql + " AND sequence_no = :sequenceNo"
    ),
    SELECT_CALENDAR_ACTIVE_LIST_ID(
        "SELECT id FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sequence_no = :sequenceNo"
    ),
    INSERT_CALENDAR_ACTIVE_LIST(
        "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + "\n" +
        "(sequence_no, calendar_no, calendar_year, calendar_date, notes, release_date_time, last_file, " +
        " modified_date_time, published_date_time)\n" +
        "VALUES (:sequenceNo, :calendarNo, :year, :calendarDate, :notes, :releaseDateTime, :lastFile, " +
        "        :modifiedDateTime, :publishedDateTime)"
    ),
    UPDATE_CALENDAR_ACTIVE_LIST(
        "UPDATE ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + "\n" +
        "SET calendar_date = :calendarDate, notes = :notes, release_date_time = :releaseDateTime, " +
        "    last_file = :lastFile, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sequence_no = :sequenceNo"
    ),
    DELETE_CALENDAR_ACTIVE_LIST(
        "DELETE FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST + "\n" +
        "WHERE calendar_no = :calendarNo AND calendar_year = :year AND sequence_no = :sequenceNo"
    ),

    /** --- Calendar Active List Entry --- */

    SELECT_CALENDAR_ACTIVE_LIST_ENTRIES(
        "SELECT * FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST_ENTRY + "\n" +
        "WHERE calendar_active_list_id IN (" + SELECT_CALENDAR_ACTIVE_LIST_ID.sql + ")"
    ),
    INSERT_CALENDAR_ACTIVE_LIST_ENTRY(
        "INSERT INTO ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST_ENTRY + "\n" +
        "(calendar_active_list_id, bill_calendar_no, bill_print_no, bill_amend_version, bill_session_year, last_file)\n" +
        "SELECT id, :billCalendarNo, :printNo, :amendVersion, :session, :lastFile\n" +
        "FROM (" + SELECT_CALENDAR_ACTIVE_LIST_ID.sql + ") cal_act_list_id"
    ),
    DELETE_CALENDAR_ACTIVE_LIST_ENTRY(
        "DELETE FROM ${schema}." + SqlTable.ALERT_CALENDAR_ACTIVE_LIST_ENTRY + "\n" +
        "WHERE calendar_active_list_id IN (" + SELECT_CALENDAR_ACTIVE_LIST_ID.sql + ")"
    );

    private String sql;

    SqlCalendarAlertQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
