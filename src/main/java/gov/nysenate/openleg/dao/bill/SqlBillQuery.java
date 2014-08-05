package gov.nysenate.openleg.dao.bill;

import gov.nysenate.openleg.dao.base.*;

public enum SqlBillQuery implements BasicSqlQuery
{
    /** --- Bill Base --- */

    SELECT_BILL(
        "SELECT * FROM ${schema}." + SqlTable.BILL + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    UPDATE_BILL(
        "UPDATE ${schema}." + SqlTable.BILL + "\n" +
        "SET title = :title, law_section = :lawSection, law_code = :lawCode, summary = :summary, active_version = :activeVersion, " +
        "    active_year = :activeYear, program_info = :programInfo, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime, last_fragment_id = :lastFragmentId \n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear"
    ),
    INSERT_BILL(
        "INSERT INTO ${schema}." + SqlTable.BILL + "\n" +
        "(print_no, session_year, title, law_section, law_code, summary, active_version, active_year, " +
        " program_info, modified_date_time, published_date_time, last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :title, :lawSection, :lawCode, :summary, :activeVersion, :activeYear, " +
        "        :programInfo, :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),

    /** --- Bill Sponsor --- */

    SELECT_BILL_SPONSOR(
        "SELECT * FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_SPONSOR(
        "INSERT INTO ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "(bill_print_no, bill_session_year, member_id, budget_bill, rules_sponsor) " +
        "VALUES (:printNo, :sessionYear, :memberId, :budgetBill, :rulesSponsor)"
    ),
    UPDATE_BILL_SPONSOR(
        "UPDATE ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "SET member_id = :memberId, budget_bill = :budgetBill, rules_sponsor = :rulesSponsor \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    DELETE_BILL_SPONSOR(
        "DELETE FROM ${schema}." + SqlTable.BILL_SPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),

    /** --- Bill Amendment --- */

    SELECT_BILL_AMENDMENTS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    UPDATE_BILL_AMENDMENT(
        "UPDATE ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "SET sponsor_memo = :sponsorMemo, act_clause = :actClause, full_text = :fullText, stricken = :stricken, " +
        "    current_committee_name = :currentCommitteeName, current_committee_action = :currentCommitteeAction, " +
        "    uni_bill = :uniBill, modified_date_time = :modifiedDateTime, " +
        "    published_date_time = :publishedDateTime, last_fragment_id = :lastFragmentId \n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND version = :version"
    ),
    INSERT_BILL_AMENDMENT(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT + "\n" +
        "(bill_print_no, bill_session_year, version, sponsor_memo, act_clause, full_text, stricken, " +
        " current_committee_name, current_committee_action, uni_bill, modified_date_time, " +
        " published_date_time, last_fragment_id)\n" +
        "VALUES(:printNo, :sessionYear, :version, :sponsorMemo, :actClause, :fullText, :stricken, " +
        "       :currentCommitteeName, :currentCommitteeAction, :uniBill, :modifiedDateTime, " +
        "       :publishedDateTime, :lastFragmentId)"
    ),

    /** --- Bill Amendment Cosponsors --- */

    SELECT_BILL_COSPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_COSPONSORS(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, member_id, sequence_no)\n" +
        "VALUES (:printNo, :sessionYear, :version, :memberId, :sequenceNo)"
    ),
    DELETE_BILL_COSPONSORS(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_COSPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),

    /** --- Bill Amendment Multi-sponsors --- */

    SELECT_BILL_MULTISPONSORS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "ORDER BY sequence_no ASC"
    ),
    INSERT_BILL_MULTISPONSORS(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + " " +
        "(bill_print_no, bill_session_year, bill_amend_version, member_id, sequence_no)\n" +
        "VALUES (:printNo, :sessionYear, :version, :memberId, :sequenceNo)"
    ),
    DELETE_BILL_MULTISPONSORS(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_MULTISPONSOR + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),

    /** --- Bill Amendment Votes --- */

    SELECT_BILL_VOTES(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + " info \n" +
        "JOIN ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + " roll ON info.id = roll.vote_id\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_VOTES_INFO(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, vote_type, vote_date, sequence_no, " +
        " modified_date_time, published_date_time) " +
        "VALUES (:printNo, :sessionYear, :version, :voteType::${schema}.vote_type, :voteDate, :sequenceNo, " +
        "        :modifiedDateTime, :publishedDateTime)"
    ),
    INSERT_BILL_VOTES_ROLL(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_ROLL + "\n" +
        "(vote_id, vote_code, member_id, member_short_name, session_year)\n" +
        "SELECT id, :voteCode::${schema}.vote_code, :memberId, :memberShortName, :sessionYear " +
        "FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo"
    ),
    DELETE_BILL_VOTES_INFO(
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_VOTE_INFO + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version\n" +
        "AND vote_date = :voteDate AND vote_type = :voteType::${schema}.vote_type AND sequence_no = :sequenceNo"
    ),

    /** --- Bill Actions --- */

    SELECT_BILL_ACTIONS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear\n" +
        "ORDER BY sequence_no ASC"
    ),
    SELECT_BILL_AMENDMENT_ACTIONS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE print_no = :printNo AND session_year = :sessionYear AND version = :version\n" +
        "ORDER BY sequence_no DESC"
    ),
    INSERT_BILL_ACTION(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, effect_date, chamber, text, sequence_no, " +
        " modified_date_time, published_date_time, last_fragment_id) \n" +
        "VALUES (:printNo, :sessionYear, :version, :effectDate, CAST(:chamber as chamber), :text, :sequenceNo, " +
        "        :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),
    DELETE_BILL_ACTION("" +
        "DELETE FROM ${schema}." + SqlTable.BILL_AMENDMENT_ACTION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version \n" +
        "      AND sequence_no = :sequenceNo"),

    /** --- Bill Same As --- */

    SELECT_BILL_SAME_AS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear AND bill_amend_version = :version"
    ),
    INSERT_BILL_SAME_AS(
        "INSERT INTO ${schema}." + SqlTable.BILL_AMENDMENT_SAME_AS + "\n" +
        "(bill_print_no, bill_session_year, bill_amend_version, same_as_bill_print_no, same_as_session_year, same_as_amend_version)\n" +
        "VALUES (:printNo, :sessionYear, :version, :sameAsPrintNo, :sameAsSessionYear, :sameAsVersion)"
    ),
    DELETE_SAME_AS_FOR_BILL(
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

    SELECT_BILL_PREVIOUS_VERSIONS(
        "SELECT * FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    ),
    INSERT_BILL_PREVIOUS_VERSION(
        "INSERT INTO ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "(bill_print_no, bill_session_year, prev_bill_print_no, prev_bill_session_year, prev_amend_version)\n" +
        "VALUES (:printNo, :sessionYear, :prevPrintNo, :prevSessionYear, :prevVersion)"
    ),
    DELETE_BILL_PREVIOUS_VERSIONS(
        "DELETE FROM ${schema}." + SqlTable.BILL_PREVIOUS_VERSION + "\n" +
        "WHERE bill_print_no = :printNo AND bill_session_year = :sessionYear"
    );

    private String sql;

    SqlBillQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String envSchema) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema);
    }

    @Override
    public String getSql(String envSchema, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema, limitOffset);
    }

    @Override
    public String getSql(String envSchema, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(sql, envSchema, orderBy, limitOffset);
    }
}
