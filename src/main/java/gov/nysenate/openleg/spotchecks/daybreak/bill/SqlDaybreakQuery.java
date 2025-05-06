package gov.nysenate.openleg.spotchecks.daybreak.bill;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlSchema;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlDaybreakQuery implements BasicSqlQuery
{
    /** --- Daybreak File --- */

    INSERT_DAYBREAK_FILE(
        "INSERT INTO " + SqlTable.DAYBREAK_FILE + "( report_date, filename, type, is_archived )" + "\n" +
        "VALUES ( :reportDate, :fileName, CAST(:fileType AS " + SqlSchema.SPOTCHECK + ".daybreak_file_type), :isArchived )"
    ),
    UPDATE_DAYBREAK_FILE(
        "UPDATE " + SqlTable.DAYBREAK_FILE + "\n" +
        "SET is_archived = :isArchived, type = CAST(:fileType AS " + SqlSchema.SPOTCHECK + ".daybreak_file_type)" + "\n" +
        "WHERE report_date = :reportDate AND filename = :fileName"
    ),
    UPDATE_DAYBREAK_FILE_ARCHIVED(
        "UPDATE " + SqlTable.DAYBREAK_FILE + "\n" +
        "SET is_archived = true" + "\n" +
        "WHERE report_date = :reportDate AND filename = :fileName"
    ),

    /** --- Daybreak Fragment --- */

    SELECT_PENDING_DAYBREAK_FRAGMENTS(
        "SELECT * FROM " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "WHERE pending_processing = true"
    ),
    INSERT_DAYBREAK_FRAGMENT(
        "INSERT INTO " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "       (  bill_print_no, bill_session_year, bill_active_version, report_date, filename, fragment_text )" + "\n" +
        "VALUES ( :billPrintNo,  :billSessionYear,  :billActiveVersion,  :reportDate, :fileName, :fragmentText )"
    ),
    UPDATE_DAYBREAK_FRAGMENT(
        "UPDATE " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET bill_active_version = :billActiveVersion, filename = :fileName, fragment_text = :fragmentText, " +
        "   pending_processing = true, modified_date_time = now()" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PROCESSED(
        "UPDATE " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET pending_processing = false, processed_count = processed_count + 1, processed_date_time = now()" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear" +
        "   AND report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PROCESSED_REPORT(
            "UPDATE " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
            "SET pending_processing = false, processed_count = processed_count + 1, processed_date_time = now()" + "\n" +
            "WHERE report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PENDING_PROCESSING(
        "UPDATE " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET pending_processing = true" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear" +
        "   AND report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PENDING_PROCESSING_REPORT(
        "UPDATE " + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET pending_processing = true" + "\n" +
        "WHERE report_date = :reportDate"
    ),

    /** --- Page File Entry --- */

    SELECT_PAGE_FILE_ENTRIES_BY_REPORT(
        "SELECT * FROM " + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "WHERE report_date = :reportDate"
    ),
    INSERT_PAGE_FILE_ENTRY(
        "INSERT INTO " + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "       (  report_date, filename,  bill_session_year, bill_publish_date, page_count, " +
        "        senate_bill_print_no, senate_bill_version, assembly_bill_print_no, assembly_bill_version )" + "\n" +
        "VALUES ( :reportDate, :fileName, :billSessionYear,  :billPublishDate,  :pageCount, " +
        "       :senateBillPrintNo,   :senateBillVersion,  :assemblyBillPrintNo,   :assemblyBillVersion )"
    ),
    UPDATE_PAGE_FILE_ENTRY(
        "UPDATE " + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "SET filename = :fileName, bill_publish_date = :billPublishDate, page_count = :pageCount, " +
        "       senate_bill_print_no = :senateBillPrintNo, senate_bill_version = :senateBillVersion, " +
        "       assembly_bill_print_no = :assemblyBillPrintNo, assembly_bill_version = :assemblyBillVersion" + "\n" +
        "WHERE report_date = :reportDate AND bill_session_year = :billSessionYear " +
        "   AND ( senate_bill_print_no = :senateBillPrintNo AND senate_bill_version = :senateBillVersion " +
        "       OR assembly_bill_print_no = :assemblyBillPrintNo AND assembly_bill_version = :assemblyBillVersion )"
    ),

    /** --- Daybreak Bill --- */

    SELECT_DAYBREAK_BILL(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    SELECT_DAYBREAK_BILL_BY_REPORT(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL + "\n" +
        "WHERE report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL(
        "INSERT INTO " + SqlTable.DAYBREAK_BILL + "\n" +
        "   (      report_date, bill_print_no, bill_session_year, " +
        "       active_version, title,  summary,        sponsor,  law_section )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear, " +
        "      :activeVersion, :title, :lawAndSummary, :sponsor, :lawSection )"
    ),
    UPDATE_DAYBREAK_BILL(
        "UPDATE " + SqlTable.DAYBREAK_BILL + "\n" +
        "SET active_version = :activeVersion, title = :title,  summary = :lawAndSummary,  " +
        "       sponsor = :sponsor,  law_section = :lawSection" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Action --- */

    SELECT_DAYBREAK_BILL_ACTIONS(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL_ACTION(
        "INSERT INTO " + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, action_date, text,  sequence_no, chamber )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :actionDate, :text, :sequenceNo, CAST(:chamber as chamber) )"
    ),
    DELETE_DAYBREAK_BILL_ACTIONS(
        "DELETE FROM " + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Amendment --- */

    SELECT_DAYBREAK_BILL_AMENDMENTS(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL_AMENDMENT(
        "INSERT INTO " + SqlTable.DAYBREAK_BILL_AMENDMENT + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, version,  publish_date, page_count, same_as )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :version, :publishDate, :pageCount, :sameAs )"
    ),
    DELETE_DAYBREAK_BILL_AMENDMENTS(
        "DELETE FROM " + SqlTable.DAYBREAK_BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Sponsor --- */

    SELECT_DAYBREAK_BILL_COSPONSORS(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('cosponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type) "
    ),
    SELECT_DAYBREAK_BILL_MULTISPONSORS(
        "SELECT * FROM " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('multisponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type) "
    ),
    INSERT_DAYBREAK_BILL_COSPONSOR(
        "INSERT INTO " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, member_short_name, type )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :memberShortName,   CAST('cosponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type) )"
    ),
    INSERT_DAYBREAK_BILL_MULTISPONSOR(
        "INSERT INTO " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, member_short_name, type )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :memberShortName,   CAST('multisponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type) )"
    ),
    DELETE_DAYBREAK_BILL_COSPONSORS(
        "DELETE FROM " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('cosponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type)"
    ),
    DELETE_DAYBREAK_BILL_MULTISPONSORS(
        "DELETE FROM " + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('multisponsor' AS " + SqlSchema.SPOTCHECK + ".sponsor_type )"
    ),

    /** --- Report --- */

    SELECT_REPORTS(
        "SELECT * FROM " + SqlTable.DAYBREAK_REPORT + "\n" +
        "WHERE report_date BETWEEN :rangeStart AND :rangeEnd "
    ),
    SELECT_REPORT_CHECKED(
        "SELECT checked FROM " + SqlTable.DAYBREAK_REPORT + "\n" +
        "WHERE report_date = :reportDate AND processed = 'TRUE'"
    ),
    INSERT_DAYBREAK_REPORT(
        "INSERT INTO " + SqlTable.DAYBREAK_REPORT + "\n" +
        "       (  report_date, processed,  checked ) " + "\n" +
        "VALUES ( :reportDate, :processed, :checked ) "
    ),
    UPDATE_DAYBREAK_REPORT(
        "UPDATE " + SqlTable.DAYBREAK_REPORT + "\n" +
        "SET processed = :processed, checked = :checked " + "\n" +
        "WHERE report_date = :reportDate"
    ),

    ;

    private String sql;

    SqlDaybreakQuery(String sql){
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
