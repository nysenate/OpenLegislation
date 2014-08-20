package gov.nysenate.openleg.dao.daybreak;

import gov.nysenate.openleg.dao.base.*;

public enum SqlDaybreakQuery implements BasicSqlQuery{

    /** --- Daybreak File --- */

    SELECT_DAYBREAK_FILE_BY_FILENAME(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FILE + "\n" +
        "WHERE report_date = :reportDate AND filename = :fileName"
    ),
    SELECT_DAYBREAK_FILE_BY_TYPE(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FILE + "\n" +
        "WHERE report_date = :reportDate AND type = CAST(:fileType AS daybreak_file_type)"
    ),
    SELECT_DAYBREAK_FILES_FROM_REPORT(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FILE + "\n" +
        "WHERE report_date = :reportDate"
    ),
    INSERT_DAYBREAK_FILE(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_FILE + "( report_date, filename, type, is_archived )" + "\n" +
        "VALUES ( :reportDate, :fileName, CAST(:fileType AS daybreak_file_type), :isArchived )"
    ),
    UPDATE_DAYBREAK_FILE(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_FILE + "\n" +
        "SET is_archived = :isArchived, type = CAST(:fileType AS daybreak_file_type)" + "\n" +
        "WHERE report_date = :reportDate AND filename = :fileName"
    ),
    UPDATE_DAYBREAK_FILE_ARCHIVED(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_FILE + "\n" +
        "SET is_archived = true" + "\n" +
        "WHERE report_date = :reportDate AND filename = :fileName"
    ),

    /** --- Daybreak Fragment --- */

    SELECT_DAYBREAK_FRAGMENT(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    SELECT_DAYBREAK_FRAGMENTS_BY_REPORT_DATE(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "WHERE report_date = :reportDate"
    ),
    SELECT_PENDING_DAYBREAK_FRAGMENTS(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "WHERE pending_processing = false"
    ),
    INSERT_DAYBREAK_FRAGMENT(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "       (  bill_print_no, bill_session_year, bill_active_version, report_date, filename, fragment_text )" + "\n" +
        "VALUES ( :billPrintNo,  :billSessionYear,  :billActiveVersion,  :reportDate, :fileName, :fragmentText )"
    ),
    UPDATE_DAYBREAK_FRAGMENT(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET bill_active_version = :billActiveVersion, filename = :fileName, fragment_text = :fragmentText" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PROCESSED(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET pending_processing = false, processed_count = processed_count + 1, processed_date_time = now()" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear" +
        "   AND report_date = :reportDate"
    ),
    UPDATE_DAYBREAK_FRAGMENT_PENDING_PROCESSING(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_FRAGMENT + "\n" +
        "SET pending_processing = true" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear" +
        "   AND report_date = :reportDate"
    ),

    /** --- Page File Entry --- */

    SELECT_PAGE_FILE_ENTRIES_BY_BILL(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "WHERE report_date = :reportDate AND bill_session_year = :billSessionYear " +
        "   AND ( senate_bill_print_no = :billPrintNo OR assembly_bill_print_no = :billPrintNo )"
    ),
    SELECT_PAGE_FILE_ENTRIES_BY_REPORT(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "WHERE report_date = :reportDate"
    ),
    INSERT_PAGE_FILE_ENTRY(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "       (  report_date, filename,  bill_session_year, bill_publish_date, page_count, " +
        "        senate_bill_print_no, senate_bill_version, assembly_bill_print_no, assembly_bill_version )" + "\n" +
        "VALUES ( :reportDate, :fileName, :billSessionYear,  :billPublishDate,  :pageCount, " +
        "       :senateBillPrintNo,   :senateBillVersion,  :assemblyBillPrintNo,   :assemblyBillVersion )"
    ),
    UPDATE_PAGE_FILE_ENTRY(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "SET filename = :fileName, bill_publish_date = :billPublishDate, page_count = :pageCount, " +
        "       senate_bill_print_no = :senateBillPrintNo, senate_bill_version = :senateBillVersion, " +
        "       assembly_bill_print_no = :assemblyBillPrintNo, assembly_bill_version = :assemblyBillVersion" + "\n" +
        "WHERE report_date = :reportDate AND bill_session_year = :billSessionYear " +
        "   AND ( senate_bill_print_no = :billPrintNo OR assembly_bill_print_no = :billPrintNo )"
    ),
    DELETE_PAGE_FILE_ENTRIES(
        "DELETE FROM ${schema}." + SqlTable.DAYBREAK_PAGE_FILE_ENTRY + "\n" +
        "WHERE report_date = :reportDate"
    ),

    /** --- Daybreak Bill --- */

    SELECT_DAYBREAK_BILL(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    SELECT_DAYBREAK_BILL_BY_REPORT(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL + "\n" +
        "WHERE report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_BILL + "\n" +
        "   (      report_date, bill_print_no, bill_session_year, " +
        "       active_version, title,  lawAndSummary,  sponsor,  law_section, law_code )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear, " +
        "      :activeVersion, :title, :lawAndSummary, :sponsor, :lawSection, :lawCode )"
    ),
    UPDATE_DAYBREAK_BILL(
        "UPDATE ${schema}." + SqlTable.DAYBREAK_BILL + "\n" +
        "SET active_version = :activeVersion, title = :title,  lawAndSummary = :lawAndSummary,  " +
        "       sponsor = :sponsor,  law_section = :lawSection, law_code = :lawCode" + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Action --- */

    SELECT_DAYBREAK_BILL_ACTIONS(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL_ACTION(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, action_date, chamber,  text,  sequence_no)" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :actionDate, :chamber, :text, :sequenceNo )"
    ),
    DELETE_DAYBREAK_BILL_ACTIONS(
        "DELETE FROM ${schema}." + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Amendment --- */

    SELECT_DAYBREAK_BILL_AMENDMENTS(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),
    INSERT_DAYBREAK_BILL_AMENDMENT(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_BILL_ACTION + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, version,  publish_date, page_count, same_as" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :version, :publishDate, :pageCount, :sameAs"
    ),
    DELETE_DAYBREAK_BILL_AMENDMENTS(
        "DELETE FROM ${schema}." + SqlTable.DAYBREAK_BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate"
    ),

    /** --- Daybreak Bill Sponsor --- */

    SELECT_DAYBREAK_BILL_COSPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('cosponsor' AS sponsor_type) "
    ),
    SELECT_DAYBREAK_BILL_MULTISPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate " +
        "   AND type = CAST('multisponsor' AS sponsor_type) "
    ),
    INSERT_DAYBREAK_BILL_COSPONSOR(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, member_short_name, type )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :memberShortName,   CAST('cosponsor' AS sponsor_type) )"
    ),
    INSERT_DAYBREAK_BILL_MULTISPONSOR(
        "INSERT INTO ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "       (  report_date, bill_print_no, bill_session_year, member_short_name, type )" + "\n" +
        "VALUES ( :reportDate, :billPrintNo,  :billSessionYear,  :memberShortName,   CAST('multisponsor' AS sponsor_type)"
    ),
    DELETE_DAYBREAK_BILL_COSPONSORS(
        "DELETE FROM ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate" +
        "   AND type = CAST('cosponsor' AS sponsor_type) "
    ),
    DELETE_DAYBREAK_BILL_MULTISPONSORS(
        "DELETE FROM ${schema}." + SqlTable.DAYBREAK_BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :billPrintNo AND bill_session_year = :billSessionYear " +
        "   AND report_date = :reportDate" +
        "   AND type = CAST('multisponsor' AS sponsor_type) "
    )

    ;

    private String sql;

    SqlDaybreakQuery(String sql){
        this.sql = sql;
    }

    @Override
    public String getSql(String envSchema) {
        return SqlQueryUtils.getSqlWithSchema(this.sql, envSchema);
    }

    @Override
    public String getSql(String envSchema, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema, limitOffset);
    }

    @Override
    public String getSql(String envSchema, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(this.sql, envSchema, orderBy, limitOffset);
    }
}
