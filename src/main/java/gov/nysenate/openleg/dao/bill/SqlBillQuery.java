package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.dao.base.SqlQueryEnum;
import gov.nysenate.openleg.dao.base.SqlTable;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum SqlBillQuery implements SqlQueryEnum
{
    /** --- Bill --- */

    SELECT_BILL_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    UPDATE_BILL_SQL(
        "UPDATE ${schema}." + SqlTable.BILL + "\n" +
        "SET title = :title, law_section = :lawSection, law_code = :lawCode, summary = :summary, active_version = :activeVersion, " +
        "    active_year = :activeYear, modified_date_time = :modifiedDateTime, published_date_time = :publishedDateTime, " +
        "    last_fragment_file_name = :lastFragmentFileName, last_fragment_type = :lastFragmentType \n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    INSERT_BILL_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL + "\n" +
        "(print_no, session_year, title, law_section, law_code, summary, active_version, active_year, " +
        " modified_date_time, published_date_time, last_fragment_file_name, last_fragment_type) \n" +
        "VALUES (:printNo, :sessionYear, :title, :lawSection, :lawCode, :summary, :activeVersion, :activeYear, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFragmentFileName, :lastFragmentType)"
    ),

    /** --- Bill Sponsor --- */

    SELECT_BILL_SPONSOR_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_SPONSOR_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "(bill_print_no, bill_session_year, member_id, budget_bill, rules_sponsor) " +
        "VALUES (:printNo, :sessionYear, :memberId, :budgetBill, :rulesSponsor)"
    ),
    UPDATE_BILL_SPONSOR_SQL(
        "UPDATE ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "SET member_id = :memberId, budget_bill = :budgetBill, rules_sponsor = :rulesSponsor \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    DELETE_BILL_SPONSOR_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Bill Amendment --- */

    SELECT_BILL_AMENDMENTS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMENDMENT_SQL(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "SET sponsor_memo = :sponsorMemo, act_clause = :actClause, full_text = :fullText, stricken = :stricken, " +
        "    current_committee_name = :currentCommitteeName, uni_bill = :uniBill, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime, last_fragment_file_name = :lastFragmentFileName, " +
        "    last_fragment_type = :lastFragmentType \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND version = :version"
    ),
    INSERT_BILL_AMENDMENT_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "(bill_print_no, bill_session_year, version, sponsor_memo, act_clause, full_text, stricken, current_committee_name, " +
        " uni_bill, modified_date_time, published_date_time, last_fragment_file_name, last_fragment_type)\n" +
        "VALUES(:printNo, :sessionYear, :version, :sponsorMemo, :actClause, :fullText, :stricken, :currentCommitteeName, " +
        "       :uniBill, :modifiedDateTime, :publishedDateTime, :lastFragmentFileName, :lastFragmentType)"
    ),

    /** --- Bill Amendment Cosponsors --- */

    SELECT_BILL_COSPONSORS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_COSPONSORS_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, member_id, sequence_no)\n" +
        "VALUES (:printNo, :sessionYear, :version, :memberId, :sequenceNo)"
    ),
    DELETE_BILL_COSPONSORS_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),

    /** --- Bill Amendment Multi-sponsors --- */

    SELECT_BILL_MULTISPONSORS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_MULTISPONSORS_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, member_id, sequence_no)\n" +
        "VALUES (:printNo, :sessionYear, :version, :memberId, :sequenceNo)"
    ),
    DELETE_BILL_MULTISPONSORS_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),

    /** --- Bill Amendment Votes --- */

    SELECT_BILL_VOTES_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " info \n" +
        "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + " roll ON info.id = roll.vote_id\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_VOTES_INFO_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, vote_type, vote_date, sequence_no, modified_date_time, published_date_time) " +
        "VALUES (:printNo, :sessionYear, :version, :voteType, :voteDate, :sequenceNo, :modifiedDateTime, :publishedDateTime)"
    ),
    INSERT_BILL_VOTES_ROLL_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + "\n" +
        "(vote_id, vote_code, member_id, member_short_name, session_year)\n" +
        "SELECT id, :voteCode::${schema}.vote_code, :memberId, :memberShortName, :sessionYear " +
        "FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType AND sequence_no = :sequenceNo"
    ),
    DELETE_BILL_VOTES_INFO_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType AND sequence_no = :sequenceNo"
    ),

    /** --- Bill Actions --- */

    SELECT_BILL_ACTIONS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear\n" +
        "ORDER BY sequence_no ASC"
    ),
    SELECT_BILL_AMENDMENT_ACTIONS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear AND version = :version\n" +
        "ORDER BY sequence_no DESC"
    ),
    INSERT_BILL_ACTION_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, effect_date, text, sequence_no, " +
        " modified_date_time, published_date_time, last_fragment_file_name, last_fragment_type) \n" +
        "VALUES (:printNo, :sessionYear, :version, :effectDate, :text, :sequenceNo, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFragmentFileName, :lastFragmentType)"
    ),
    DELETE_BILL_ACTION_SQL("" +
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version \n" +
        "      AND sequence_no = :sequenceNo"),

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
    ),

    /** --- Bill Committee --- */

    SELECT_BILL_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "( bill_print_no, bill_session_year, committee_name, committee_chamber, action_date )" + "\n" +
        "VALUES ( :printNo, :sessionYear, :committeeName, CAST(:committeeChamber as chamber), :actionDate )"
    ),
    DELETE_BILL_COMMITTEES(
        "DELETE FROM ${schema}." + SqlTable.BILL_COMMITTEE + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Bill Previous Version --- */

    SELECT_BILL_PREVIOUS_VERSIONS_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_PREVIOUS_VERSION_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "(bill_print_no, bill_session_year, prev_bill_print_no, prev_bill_session_year, prev_amend_version)\n" +
        "VALUES (:printNo, :sessionYear, :prevPrintNo, :prevSessionYear, :prevVersion)"
    ),
    DELETE_BILL_PREVIOUS_VERSIONS_SQL(
        "DELETE FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    );

    private String sql;

    SqlBillQuery(String sql) {
        this.sql = sql;
    }

    public String getSql(String environmentSchema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", environmentSchema);
        return new StrSubstitutor(replaceMap).replace(this.sql);
    }
}
