package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.dao.base.SqlTable;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum SqlBillQuery
{
    /** --- Bill --- */

    SELECT_BILL_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    UPDATE_BILL_SQL(
        "UPDATE ${schema}." + SqlTable.BILL + "\n" +
        "SET title = :title, law_section = :lawSection, law_code = :lawCode, summary = :summary, active_version = :activeVersion, " +
        "    sponsor_id = :sponsorId, active_year = :activeYear, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime, last_fragment_file_name = :lastFragmentFileName," +
        "    last_fragment_type = :lastFragmentType \n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    INSERT_BILL_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL + "\n" +
        "(print_no, session_year, title, law_section, law_code, summary, active_version, sponsor_id, active_year, " +
        " modified_date_time, published_date_time, last_fragment_file_name, last_fragment_type) \n" +
        "VALUES (:printNo, :sessionYear, :title, :lawSection, :lawCode, :summary, :activeVersion, :sponsorId, :activeYear, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFragmentFileName, :lastFragmentType)"
    ),

    /** --- Bill Amendment --- */

    SELECT_BILL_AMENDMENTS_SQL(
            "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
                    "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMENDMENT_SQL(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "SET sponsor_memo = :sponsorMemo, act_clause = :actClause, full_text = :fullText, stricken = :stricken, " +
        "    current_committee_id = :currentCommitteeId, uni_bill = :uniBill, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime, last_fragment_file_name = :lastFragmentFileName, " +
        "    last_fragment_type = :lastFragmentType \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND version = :version"
    ),
    INSERT_BILL_AMENDMENT_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "(bill_print_no, bill_session_year, version, sponsor_memo, act_clause, full_text, stricken, current_committee_id, " +
        " uni_bill, modified_date_time, published_date_time, last_fragment_file_name, last_fragment_type)\n" +
        "VALUES(:printNo, :sessionYear, :version, :sponsorMemo, :actClause, :fullText, :stricken, :currentCommitteeId, " +
        "       :uniBill, :modifiedDateTime, :publishedDateTime, :lastFragmentFileName, :lastFragmentType)"
    ),

    /** --- Bill Actions --- */

    SELECT_BILL_ACTIONS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    SELECT_BILL_AMENDMENT_ACTIONS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear AND version = :version"
    ),
    INSERT_BILL_ACTION_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, effect_date, text, modified_date_time, published_date_time," +
        " last_fragment_file_name, last_fragment_type) \n" +
        "VALUES (:printNo, :sessionYear, :version, :effectDate, :text, :modifiedDateTime, :publishedDateTime, " +
        "        :lastFragmentFileName, :lastFragmentType)"
    ),
    DELETE_BILL_ACTION_SQL("" +
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version \n" +
        "      AND effect_date = :effectDate AND text = :text"),

    /** --- Bill Same As --- */

    SELECT_BILL_SAME_AS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_SAME_AS_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, same_as_bill_print_no, same_as_session_year, same_as_amend_version)\n" +
        "VALUES (:printNo, :sessionYear, :version, :sameAsPrintNo, :sameAsSessionYear, :sameAsVersion)"
    ),
    DELETE_SAME_AS_FOR_BILL_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    );

    private String sql;

    SqlBillQuery(String sql) {
        this.sql = sql;
    }

    public String getSql(String schema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", schema);
        StrSubstitutor sub = new StrSubstitutor(replaceMap);
        return sub.replace(this.sql);
    }
}
